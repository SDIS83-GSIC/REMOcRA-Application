package remocra.usecase.site

import jakarta.inject.Inject
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureCollection
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.ImportGeometriesCodeLibelleData
import remocra.data.enums.ErrorType
import remocra.db.SiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Site
import remocra.eventbus.EventBus
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.ImportShapeUtils
import java.io.File
import java.io.InputStream
import java.util.UUID

class ImportSitesUseCase : AbstractUseCase() {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var importShapeUtils: ImportShapeUtils

    @Inject
    lateinit var siteRepository: SiteRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var appSettings: AppSettings

    fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.SITE_FORBIDDEN_UPDATE)
        }
    }

    fun execute(userInfo: WrappedUserInfo, element: ImportGeometriesCodeLibelleData): Result {
        checkDroits(userInfo)
        return try {
            Result.Success(importSites(element.fileGeometries, userInfo))
        } catch (rre: RemocraResponseException) {
            Result.Error(rre.message)
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }

    private fun importSites(inputStream: InputStream, userInfo: WrappedUserInfo) {
        val fileShp: File = importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_IMPORT_SITES)
            ?: throw RemocraResponseException(ErrorType.IMPORT_SITES_SHP_INTROUVABLE)

        val store = FileDataStoreFinder.getDataStore(fileShp)
        val source = store.featureSource

        source.features.let { features: SimpleFeatureCollection ->
            val iterator = features.features()
            while (iterator.hasNext()) {
                val next = iterator.next()

                val geometrie: Geometry =
                    (next.properties.find { it.name.localPart == "the_geom" }?.value as Geometry?)?.getGeometryN(0)
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SITES_GEOMETRIE_NULLE)

                geometrie.srid = appSettings.srid

                val code: String =
                    next.properties.find { it.name.localPart == "code" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SITES_CODE_NULL)

                val libelle: String =
                    next.properties.find { it.name.localPart == "libelle" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SITES_LIBELLE_NULL)

                val site = siteRepository.upsertSite(Site(siteId = UUID.randomUUID(), siteActif = true, siteCode = code, siteLibelle = libelle, siteGeometrie = geometrie, null))

                eventBus.post(
                    TracabiliteEvent(
                        pojo = site,
                        pojoId = site.siteId,
                        typeOperation = TypeOperation.UPDATE,
                        typeObjet = TypeObjet.SITE,
                        auteurTracabilite = userInfo.getInfosTracabilite(),
                        date = dateUtils.now(),
                    ),
                )
            }
        }

        // On supprime les fichiers du disque
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_IMPORT_SITES)
    }
}
