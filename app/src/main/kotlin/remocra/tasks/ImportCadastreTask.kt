package remocra.tasks

import jakarta.inject.Inject
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.feature.simple.SimpleFeature
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.enums.CodeSdis
import remocra.data.enums.ErrorType
import remocra.db.CadastreRepository
import remocra.db.CommuneRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeTask
import remocra.db.jooq.remocra.tables.pojos.CadastreParcelle
import remocra.db.jooq.remocra.tables.pojos.CadastreSection
import remocra.exception.RemocraResponseException
import remocra.usecase.document.DocumentUtils
import remocra.utils.ImportShapeUtils
import java.io.InputStream
import java.net.URI
import java.nio.file.Path
import java.util.UUID
import kotlin.collections.mutableMapOf

class ImportCadastreParameters() : TaskParameters(notification = null)

class ImportCadastreTask @Inject constructor(
    private val appSettings: AppSettings,
    private val importShapeUtils: ImportShapeUtils,
    private val communeRepository: CommuneRepository,
    private val documentUtils: DocumentUtils,
    private val cadastreRepository: CadastreRepository,
) : SimpleTask<ImportCadastreParameters, JobResults>() {

    companion object {
        private const val BASE_URL_CADASTRE = "https://files.data.gouv.fr/cadastre/etalab-cadastre/"
        private const val FICHIER_PARCELLES_SUFFIX = "-parcelles-shp.zip"
        private const val FICHIER_SECTIONS_SUFFIX = "-sections-shp.zip"
        private const val MILLESIME = "2023-01-01"

        private const val CODE_SDIS_PREFIX = "SDIS_"
        private const val PROPERTY_GEO = "the_geom"
        private const val PROPERTY_COMMUNE = "commune"
        private const val PROPERTY_PREFIXE = "prefixe"
        private const val PROPERTY_ID = "id"
        private const val PROPERTY_SECTION = "section"
        private const val PROPERTY_NUMERO = "numero"
        private const val PROPERTY_CODE = "code"
        private const val END_PROPERTY = "000"
        private const val NUMERO_LENGTH = 2
        private val CODE_SDIS_BSPP = setOf("75", "92", "93", "94")
        private val CODE_SDIS_SDMIS = setOf("69")
    }

    private fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_PARAM_APPLI)) {
            throw RemocraResponseException(ErrorType.ADMIN_PARAMETRE_FORBIDDEN)
        }
    }

    private fun deleteTemporaryFiles() {
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_IMPORT_CADASTRE)
    }

    private fun retrieveShpFileFromZip(inputStream: InputStream): Path {
        return importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_IMPORT_CADASTRE)
            ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_SHP_INTROUVABLE)
    }

    private fun extractGeometry(feature: SimpleFeature): Geometry? {
        return (feature.properties.find { it.name.localPart == PROPERTY_GEO }?.value as Geometry?)
            ?.getGeometryN(0).apply { this?.srid = appSettings.srid }
    }

    private fun extractStringProperty(feature: SimpleFeature, property: String): String? {
        return feature.properties.find { it.name.localPart == property }?.value?.toString()
    }

    private fun processShpFile(fileStream: InputStream, featureAction: (SimpleFeature) -> Unit) {
        val fileShp = retrieveShpFileFromZip(fileStream)
        val iterator = FileDataStoreFinder.getDataStore(fileShp.toFile()).featureSource.features.features()
        while (iterator.hasNext()) {
            val feature = iterator.next()
            try {
                featureAction(feature)
            } catch (e: Exception) {
                logManager.error("Erreur lors du traitement des fonctionnalités : ${e.message}")
            }
        }
    }

    private data class SectionIdMetier(
        val codeInsee: String,
        val prefixe: String,
        val numero: String,
    )

    private fun importCadastreSection(
        feature: SimpleFeature,
        mapCommuneIdByCodeInsee: MutableMap<String, UUID>,
        mapSectionByCode: MutableMap<SectionIdMetier, UUID>,
    ) {
        val geometrie = extractGeometry(feature) ?: return
        val codeInseeCommune = extractStringProperty(feature, PROPERTY_COMMUNE) ?: return
        val prefixe = extractStringProperty(feature, PROPERTY_PREFIXE) ?: END_PROPERTY
        val id = extractStringProperty(feature, PROPERTY_ID)

        // TODO vérifier, typiquement on n'utilise pas le préfixe...
        // l'id est code insee + préfixe + code (avec le pad sur 2 car), voir si on veut stocker seulement le numéro ou le code complet
        val numero = extractStringProperty(feature, PROPERTY_CODE)?.padStart(NUMERO_LENGTH, '0') ?: logManager.error("Numéro non trouvé pour la section pour la section $id").let { return }

        val communeId = if (mapCommuneIdByCodeInsee.contains(codeInseeCommune)) {
            mapCommuneIdByCodeInsee[codeInseeCommune]!!
        } else {
            val commune = communeRepository.getByCodeInsee(codeInseeCommune) ?: logManager.error("Commune introuvable pour la section $id et le code INSEE $codeInseeCommune").let { return }
            mapCommuneIdByCodeInsee[codeInseeCommune] = commune.communeId
            commune.communeId
        }

        val section = CadastreSection(
            cadastreSectionId = UUID.randomUUID(),
            cadastreSectionGeometrie = geometrie,
            cadastreSectionNumero = numero,
            cadastreSectionCommuneId = communeId,
        )

        mapSectionByCode[SectionIdMetier(codeInseeCommune, prefixe, numero)] = section.cadastreSectionId
        cadastreRepository.insertSection(section)
    }

    private fun importCadastreParcelle(
        feature: SimpleFeature,
        mapSectionByCode: MutableMap<SectionIdMetier, UUID>,
    ) {
        val geometrie = extractGeometry(feature) ?: return
        val codeInseeCommune = extractStringProperty(feature, PROPERTY_COMMUNE) ?: return
        val prefixe = extractStringProperty(feature, PROPERTY_PREFIXE) ?: END_PROPERTY
        val id = extractStringProperty(feature, PROPERTY_ID)

        val numero = extractStringProperty(feature, PROPERTY_NUMERO) ?: logManager.error("Numéro non trouvé pour la section pour la section $id").let { return }

        val sectionNumero = extractStringProperty(feature, PROPERTY_SECTION)?.padStart(NUMERO_LENGTH, '0') ?: logManager.error("Numéro non trouvé pour la section pour la section $id").let { return }
        val sectionId = mapSectionByCode[SectionIdMetier(codeInsee = codeInseeCommune, prefixe = prefixe, numero = sectionNumero)] ?: throw RemocraResponseException(ErrorType.SECTION_NOT_FOUND)

        val parcelle = CadastreParcelle(
            cadastreParcelleId = UUID.randomUUID(),
            cadastreParcelleGeometrie = geometrie,
            cadastreParcelleNumero = numero,
            cadastreParcelleCadastreSectionId = sectionId,
        )

        cadastreRepository.insertParcelle(parcelle)
    }

    private fun downloadAndImportCadastreFiles(departement: String) {
        val departementUrl = "$BASE_URL_CADASTRE/$MILLESIME/shp/departements/$departement/"

        val fichierSections = "cadastre-$departement$FICHIER_SECTIONS_SUFFIX"
        val fichierParcelles = "cadastre-$departement$FICHIER_PARCELLES_SUFFIX"

        val mapCommuneIdByCodeInsee = mutableMapOf<String, UUID>()
        val mapSectionByCode = mutableMapOf<SectionIdMetier, UUID>()

        URI(departementUrl.plus(fichierSections)).toURL().openStream().use {
            processShpFile(it) { feature ->
                importCadastreSection(feature, mapCommuneIdByCodeInsee, mapSectionByCode)
            }
        }

        URI(departementUrl.plus(fichierParcelles)).toURL().openStream().use {
            processShpFile(it) { feature ->
                importCadastreParcelle(feature, mapSectionByCode)
            }
        }

        deleteTemporaryFiles()
    }

    /**
     * Importe le cadastre depuis les fichiers shapefiles disponibles sur data.gouv.fr
     * On prend le dernier millésime disponible, et on décline par département.
     */
    override fun execute(parameters: ImportCadastreParameters?, userInfo: WrappedUserInfo): JobResults {
        checkDroits(userInfo)

        getDepartementsForSdis().forEach { departement ->
            try {
                downloadAndImportCadastreFiles(departement)
            } catch (e: Exception) {
                logManager.error("Erreur lors du traitement des départements $departement: ${e.message}")
            }
        }

        // On ne retourne pas de résultats spécifiques pour cette tâche
        return JobResults()
    }

    override fun checkParameters(parameters: ImportCadastreParameters?) {
        // no-op
    }

    override fun getType(): TypeTask {
        return TypeTask.IMPORT_CADASTRE
    }

    override fun getTaskParametersClass(): Class<ImportCadastreParameters> {
        return ImportCadastreParameters::class.java
    }

    override fun notifySpecific(executionResults: JobResults?, notificationRaw: NotificationRaw) {
        // Pas de notification spécifique pour cette tâche
    }

    private fun getDepartementsForSdis(): Set<String> {
        return when (appSettings.codeSdis) {
            // Liste des départements spécifiques à chaque SDIS
            CodeSdis.SDIS_01, CodeSdis.SDIS_09, CodeSdis.SDIS_16, CodeSdis.SDIS_21,
            CodeSdis.SDIS_22, CodeSdis.SDIS_38, CodeSdis.SDIS_39, CodeSdis.SDIS_42,
            CodeSdis.SDIS_49, CodeSdis.SDIS_53, CodeSdis.SDIS_58, CodeSdis.SDIS_59,
            CodeSdis.SDIS_61, CodeSdis.SDIS_62, CodeSdis.SDIS_66, CodeSdis.SDIS_71,
            CodeSdis.SDIS_77, CodeSdis.SDIS_78, CodeSdis.SDIS_83, CodeSdis.SDIS_89,
            CodeSdis.SDIS_95, CodeSdis.SDIS_971, CodeSdis.SDIS_973,
            -> setOf(appSettings.codeSdis.name.removePrefix(CODE_SDIS_PREFIX))
            CodeSdis.BSPP -> CODE_SDIS_BSPP
            CodeSdis.SDMIS -> CODE_SDIS_SDMIS
        }
    }
}
