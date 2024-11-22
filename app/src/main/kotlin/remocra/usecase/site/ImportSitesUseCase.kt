package remocra.usecase.site

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
import remocra.data.ImportSitesData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
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
import java.io.File
import java.io.InputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportSitesUseCase : AbstractUseCase() {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var siteRepository: SiteRepository

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var appSettings: AppSettings

    fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.SITE_FORBIDDEN_UPDATE)
        }
    }

    fun execute(userInfo: UserInfo?, element: ImportSitesData): Result? {
        // TODO ne plus rendre nullable lorsque tous les cas d'utilisation seront développés !
        if (userInfo == null) {
            throw ForbiddenException()
        }
        checkDroits(userInfo)

        var result: Result? = null
        try {
            importSites(element.fileSites, userInfo)
        } catch (rre: RemocraResponseException) {
            result = Result.Error(rre.message)
        } catch (e: Exception) {
            result = Result.Error(e.message)
        }
        return result
    }

    private fun importSites(inputStream: InputStream, userInfo: UserInfo) {
        val fileShp: File = readZipFile(inputStream)
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
                        auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                        date = dateUtils.now(),
                    ),
                )
            }
        }

        // On supprime les fichiers du disque
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_IMPORT_SITES)
    }

    // TODO au Directory près, peut être utilitarisé
    private fun readZipFile(inputStream: InputStream): File? {
        var fileShp: File? = null
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                documentUtils.ensureDirectory(
                    GlobalConstants.DOSSIER_TMP_IMPORT_SITES,
                )

                val file = File(GlobalConstants.DOSSIER_TMP_IMPORT_SITES + zipEntry.name)

                zipInputStream.readBytes().inputStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                if (zipEntry.name.contains(".shp")) {
                    fileShp = file
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
        return fileShp
    }
}
