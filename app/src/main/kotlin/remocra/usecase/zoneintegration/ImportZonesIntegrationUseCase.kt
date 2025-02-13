package remocra.usecase.zoneintegration

import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureCollection
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.ImportGeometriesCodeLibelleData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.ZoneIntegrationRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeZoneIntegration
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import remocra.eventbus.EventBus
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.ImportShapeUtils
import java.io.File
import java.io.InputStream
import java.util.UUID

class ImportZonesIntegrationUseCase : AbstractUseCase() {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var importShapeUtils: ImportShapeUtils

    @Inject
    lateinit var zoneIntegrationRepository: ZoneIntegrationRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var appSettings: AppSettings

    fun checkDroits(userInfo: UserInfo) {
        // TODO quel droit pour l'administration des zones de compétence / intégration ?
        if (!userInfo.droits.contains(Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ZONE_INTEGRATION_FORBIDDEN_UPDATE)
        }
    }

    fun execute(userInfo: UserInfo?, element: ImportGeometriesCodeLibelleData): Result {
        // TODO ne plus rendre nullable lorsque tous les cas d'utilisation seront développés !
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        return try {
            Result.Success(importZonesIntegration(element.fileGeometries, userInfo))
        } catch (rre: RemocraResponseException) {
            Result.Error(rre.message)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    private fun importZonesIntegration(inputStream: InputStream, userInfo: UserInfo) {
        val fileShp: File = importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_IMPORT_ZONES_INTEGRATION)
            ?: throw RemocraResponseException(ErrorType.IMPORT_ZONES_INTEGRATION_SHP_INTROUVABLE)

        val store = FileDataStoreFinder.getDataStore(fileShp)
        val source = store.featureSource

        source.features.let { features: SimpleFeatureCollection ->
            val iterator = features.features()
            while (iterator.hasNext()) {
                val next = iterator.next()

                val geometrie: Geometry =
                    (next.properties.find { it.name.localPart == "the_geom" }?.value as Geometry?)?.getGeometryN(0)
                        ?: throw RemocraResponseException(ErrorType.IMPORT_ZONES_INTEGRATION_GEOMETRIE_NULLE)

                geometrie.srid = appSettings.srid

                val code: String =
                    next.properties.find { it.name.localPart == "code" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_ZONES_INTEGRATION_CODE_NULL)

                val libelle: String =
                    next.properties.find { it.name.localPart == "libelle" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_ZONES_INTEGRATION_LIBELLE_NULL)

                val zoneIntegration = zoneIntegrationRepository.upsertZoneIntegration(
                    ZoneIntegration(
                        zoneIntegrationId = UUID.randomUUID(),
                        zoneIntegrationActif = true,
                        zoneIntegrationCode = code,
                        zoneIntegrationLibelle = libelle,
                        zoneIntegrationGeometrie = geometrie,
                        TypeZoneIntegration.ZONE_COMPETENCE,
                    ),
                )

                eventBus.post(
                    TracabiliteEvent(
                        pojo = zoneIntegration,
                        pojoId = zoneIntegration.zoneIntegrationId,
                        typeOperation = TypeOperation.UPDATE,
                        typeObjet = TypeObjet.ZONE_INTEGRATION,
                        auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                        date = dateUtils.now(),
                    ),
                )
            }
        }

        // On supprime les fichiers du disque
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_IMPORT_ZONES_INTEGRATION)
    }
}
