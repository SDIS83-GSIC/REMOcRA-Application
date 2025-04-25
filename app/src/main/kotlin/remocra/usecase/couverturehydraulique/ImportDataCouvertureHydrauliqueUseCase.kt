package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.data.simple.SimpleFeatureCollection
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.couverturehydraulique.Batiment
import remocra.data.couverturehydraulique.Reseau
import remocra.data.couverturehydraulique.ReseauBatimentPeiProjet
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.couverturehydraulique.tables.pojos.PeiProjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import remocra.utils.ImportShapeUtils
import java.io.File
import java.io.InputStream
import java.util.UUID

class ImportDataCouvertureHydrauliqueUseCase : AbstractCUDUseCase<ReseauBatimentPeiProjet>(TypeOperation.UPDATE) {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    @Inject lateinit var dataCacheProvider: DataCacheProvider

    @Inject lateinit var appSettings: AppSettings

    @Inject
    lateinit var importShapeUtils: ImportShapeUtils

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: ReseauBatimentPeiProjet, userInfo: WrappedUserInfo) {
        // On ne trace pas l'action
    }

    override fun execute(userInfo: WrappedUserInfo, element: ReseauBatimentPeiProjet): ReseauBatimentPeiProjet {
        if (element.fileReseau != null) {
            importReseau(element.fileReseau, element.etudeId)
        }

        if (element.fileBatiment != null) {
            importBatiment(element.fileBatiment, element.etudeId)
        }

        if (element.filePeiProjet != null) {
            importPeiProjet(element.filePeiProjet, element.etudeId)
        }

        return element
    }

    private fun importReseau(inputStream: InputStream, etudeId: UUID) {
        val fileShp: File = importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)
            ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_SHP_INTROUVABLE)

        val store = FileDataStoreFinder.getDataStore(fileShp)
        val source = store.featureSource

        val listReseau = mutableListOf<Reseau>()

        source.features.let { features: SimpleFeatureCollection ->
            val iterator = features.features()
            while (iterator.hasNext()) {
                val next = iterator.next()

                val geometrie: Geometry =
                    (next.properties.find { it.name.localPart == "the_geom" }?.value as Geometry?)?.getGeometryN(0)
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_GEOMETRIE_NULLE)

                geometrie.srid = appSettings.srid

                val reseauTraversable: Boolean =
                    next.properties.find { it.name.localPart == "traversabl" }?.value?.toString()?.toBooleanStrictOrNull() ?: false
                val reseauSensUnique: Boolean =
                    next.properties.find { it.name.localPart == "sensUnique" }?.value?.toString()?.toBooleanStrictOrNull() ?: false
                val reseauNiveau: Int? = next.properties.find { it.name.localPart == "niveau" }?.value?.toString()?.toIntOrNull()

                listReseau.add(
                    Reseau(
                        reseauGeometrie = geometrie,
                        reseauTraversable = reseauTraversable,
                        reseauSensUnique = reseauSensUnique,
                        reseauNiveau = reseauNiveau,
                    ),
                )
            }
        }

        // On supprime les fichiers du disque
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)

        // On supprime l'ancien réseau
        couvertureHydrauliqueRepository.deleteReseauByEtudeId(etudeId)

        // Puis on insère
        couvertureHydrauliqueRepository.insertReseau(etudeId, listReseau)
    }

    private fun importBatiment(inputStream: InputStream, etudeId: UUID) {
        val fileShp: File = importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)
            ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_SHP_INTROUVABLE)
        val store = FileDataStoreFinder.getDataStore(fileShp)
        val source = store.featureSource

        val listBatiment = mutableListOf<Batiment>()

        source.features.let { features: SimpleFeatureCollection ->
            val iterator = features.features()
            while (iterator.hasNext()) {
                val next = iterator.next()

                val geometrie: Geometry =
                    next.properties.find { it.name.localPart == "the_geom" }?.value as Geometry?
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_GEOMETRIE_NULLE)

                geometrie.srid = appSettings.srid

                listBatiment.add(
                    Batiment(
                        batimentGeometrie = geometrie,
                    ),
                )
            }

            // On supprime les fichiers du disque
            documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)

            // Puis on supprime les anciennes valeurs et on insère les nouvelles
            couvertureHydrauliqueRepository.deleteBatimentByEtudeId(etudeId)
            couvertureHydrauliqueRepository.insertBatiment(etudeId, listBatiment)
        }
    }

    private fun importPeiProjet(inputStream: InputStream, etudeId: UUID) {
        val fileShp: File = importShapeUtils.readZipFile(inputStream, GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)
            ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_SHP_INTROUVABLE)

        val store = FileDataStoreFinder.getDataStore(fileShp)
        val source = store.featureSource

        val listPeiProjet = mutableListOf<PeiProjet>()

        source.features.let { features: SimpleFeatureCollection ->
            val iterator = features.features()
            while (iterator.hasNext()) {
                val next = iterator.next()

                // On lit les données
                val geometrie: Geometry =
                    next.properties.find { it.name.localPart == "the_geom" }?.value as Geometry?
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_GEOMETRIE_NULLE_POINT)

                if (geometrie.geometryType != "POINT") {
                    throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_GEOMETRIE_NULLE_POINT)
                }

                geometrie.srid = appSettings.srid

                val codeNatureDeci: String =
                    next.properties.find { it.name.localPart == "natureDeci" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_NATURE_DECI)
                val type: String =
                    next.properties.find { it.name.localPart == "type" }?.value?.toString()
                        ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_TYPE_PEI_PROJET)
                val codeDiametre: String? =
                    next.properties.find { it.name.localPart == "diametreNo" }?.value.toString()
                var diametreCanalisation: Int? =
                    next.properties.find { it.name.localPart == "diametreCa" }?.value?.toString()?.toIntOrNull()
                var capacite: Int? =
                    next.properties.find { it.name.localPart == "capacite" }?.value?.toString()?.toIntOrNull()
                var debit: Int? =
                    next.properties.find { it.name.localPart == "debit" }?.value?.toString()?.toIntOrNull()

                // On vérifie que le type et la nature DECI sont cohérents
                val typePeiProjet: TypePeiProjet = TypePeiProjet.entries.find { it.name == type }
                    ?: throw RemocraResponseException(ErrorType.IMPORT_SHP_TYPE_PEI_ABSENT, TypePeiProjet.entries.toString())

                val natureDeciId = dataCacheProvider.getNaturesDeci().values.firstOrNull { it.natureDeciCode == codeNatureDeci }?.natureDeciId
                    ?: throw RemocraResponseException(
                        ErrorType.IMPORT_SHP_CODE_NATURE_DECI_ABSENT,
                        dataCacheProvider.getNaturesDeci().values.map { it.natureDeciCode }.toString(),
                    )

                // On vérifie toutes les contraintes selon le type
                var diametreId: UUID? = null
                when (typePeiProjet) {
                    TypePeiProjet.PIBI -> {
                        diametreId = dataCacheProvider.getDiametres().values.firstOrNull { it.diametreCode == codeDiametre }?.diametreId
                            ?: throw RemocraResponseException(
                                ErrorType.IMPORT_SHP_CODE_DIAMETRE_ABSENT,
                                dataCacheProvider.getDiametres().values.map { it.diametreCode }.toString(),
                            )

                        if (diametreCanalisation == null) {
                            throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_DIAMETRE_MANQUANT)
                        }
                        capacite = null
                        debit = null
                    }
                    TypePeiProjet.RESERVE -> {
                        if (capacite == null) {
                            throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_CAPACITE_MANQUANTE)
                        }

                        if (debit == null) {
                            throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_DEBIT_MANQUANT_RESERVE)
                        }

                        diametreCanalisation = null
                    }
                    TypePeiProjet.PA -> {
                        if (debit == null) {
                            throw RemocraResponseException(ErrorType.IMPORT_SHP_ETUDE_DEBIT_MANQUANT_PA)
                        }
                        capacite = null
                        diametreCanalisation = null
                    }
                }

                listPeiProjet.add(
                    PeiProjet(
                        peiProjetId = UUID.randomUUID(),
                        peiProjetGeometrie = geometrie,
                        peiProjetNatureDeciId = natureDeciId,
                        peiProjetTypePeiProjet = typePeiProjet,
                        peiProjetDiametreId = diametreId,
                        peiProjetDiametreCanalisation = diametreCanalisation,
                        peiProjetCapacite = capacite,
                        peiProjetDebit = debit,
                        peiProjetEtudeId = etudeId,
                    ),
                )
            }
        }

        // On supprime les fichiers du disque
        documentUtils.deleteDirectory(GlobalConstants.DOSSIER_TMP_COUVERTURE_HYDRAULIQUE)

        // Puis on supprime les anciennes valeurs et on insère les nouvelles
        couvertureHydrauliqueRepository.deletePeiProjetByEtudeId(etudeId)
        couvertureHydrauliqueRepository.insertPeiProjet(listPeiProjet)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ReseauBatimentPeiProjet) {
        // noop -> les vérifications sont faites dans le execute au cas par cas
    }
}
