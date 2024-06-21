package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.NomenclaturesData
import remocra.data.enums.TypeNomenclature
import remocra.db.CommuneRepository
import remocra.db.DiametreRepository
import remocra.db.DomaineRepository
import remocra.db.MarquePibiRepository
import remocra.db.MateriauRepository
import remocra.db.ModelePibiRepository
import remocra.db.NatureDeciRepository
import remocra.db.NatureRepository
import remocra.db.NiveauRepository
import remocra.db.TypeCanalisationRepository
import remocra.db.TypeReseauRepository
import remocra.db.jooq.tables.pojos.Diametre
import remocra.db.jooq.tables.pojos.Domaine
import remocra.db.jooq.tables.pojos.MarquePibi
import remocra.db.jooq.tables.pojos.Materiau
import remocra.db.jooq.tables.pojos.ModelePibi
import remocra.db.jooq.tables.pojos.Nature
import remocra.db.jooq.tables.pojos.NatureDeci
import remocra.db.jooq.tables.pojos.Niveau
import remocra.db.jooq.tables.pojos.TypeCanalisation
import remocra.db.jooq.tables.pojos.TypeReseau

/**
 * Classe permettant de fournir toutes les nomenclatures stockées en cache dans REMOcRA
 */
@Singleton
class NomenclaturesProvider
@Inject
constructor(
    private val communeRepository: CommuneRepository,
    private val diametreRepository: DiametreRepository,
    private val domaineRepository: DomaineRepository,
    private val marquePibiRepository: MarquePibiRepository,
    private val materiauRepository: MateriauRepository,
    private val modelePibiRepository: ModelePibiRepository,
    private val natureRepository: NatureRepository,
    private val natureDeciRepository: NatureDeciRepository,
    private val niveauRepository: NiveauRepository,
    private val typeCanalisationRepository: TypeCanalisationRepository,
    private val typeReseauRepository: TypeReseauRepository,

) : Provider<NomenclaturesData> {
    private lateinit var nomenclaturesData: NomenclaturesData
    override fun get(): NomenclaturesData {
        if (!this::nomenclaturesData.isInitialized) {
            nomenclaturesData = buildNomenclatureData()
        }
        return nomenclaturesData
    }

    /**
     * Permet de reconstruire le cache sur un type particulier (suite à la modification d'un élément).
     */
    fun reloadNomenclature(typeToReload: TypeNomenclature) {
        when (typeToReload) {
            TypeNomenclature.DIAMETRE -> nomenclaturesData.mapDiametre = diametreRepository.getMapById()
            TypeNomenclature.DOMAINE -> nomenclaturesData.mapDomaine = domaineRepository.getMapById()
            TypeNomenclature.MARQUE_PIBI -> nomenclaturesData.mapMarquePibi = marquePibiRepository.getMapById()
            TypeNomenclature.MATERIAU -> nomenclaturesData.mapMateriau = materiauRepository.getMapById()
            TypeNomenclature.MODELE_PIBI -> nomenclaturesData.mapModelePibi = modelePibiRepository.getMapById()
            TypeNomenclature.NATURE -> nomenclaturesData.mapNature = natureRepository.getMapById()
            TypeNomenclature.NATURE_DECI -> nomenclaturesData.mapNatureDeci = natureDeciRepository.getMapById()
            TypeNomenclature.NIVEAU -> nomenclaturesData.mapNiveau = niveauRepository.getMapById()
            TypeNomenclature.TYPE_CANALISATION -> nomenclaturesData.mapTypeCanalisation = typeCanalisationRepository.getMapById()
            TypeNomenclature.TYPE_RESEAU -> nomenclaturesData.mapTypeReseau = typeReseauRepository.getMapById()
        }
    }

    /**
     * Charge les données dans le cache pour utilisation ultérieure.
     */
    private fun buildNomenclatureData(): NomenclaturesData {
//        val communes = communeRepository.getMapById()
        val diametres = diametreRepository.getMapById()
        val domaines = domaineRepository.getMapById()
        val marquesPibi = marquePibiRepository.getMapById()
        val materiaux = materiauRepository.getMapById()
        val modelesPibi = modelePibiRepository.getMapById()
        val nature = natureRepository.getMapById()
        val natureDeci = natureDeciRepository.getMapById()
        val niveau = niveauRepository.getMapById()
        val typeCanalisation = typeCanalisationRepository.getMapById()
        val typeReseau = typeReseauRepository.getMapById()

        return NomenclaturesData(
            // mapCommune = communes,
            mapDiametre = diametres,
            mapDomaine = domaines,
            mapMateriau = materiaux,
            mapMarquePibi = marquesPibi,
            mapModelePibi = modelesPibi,
            mapNature = nature,
            mapNatureDeci = natureDeci,
            mapNiveau = niveau,
            mapTypeCanalisation = typeCanalisation,
            mapTypeReseau = typeReseau,
        )
    }

    /**
     * Permet de retourner une map de nomenclature en fonction de son type.
     */
    fun getData(typeNomenclature: TypeNomenclature) = when (typeNomenclature) {
        TypeNomenclature.DIAMETRE -> get().mapDiametre
        TypeNomenclature.DOMAINE -> get().mapDomaine
        TypeNomenclature.MARQUE_PIBI -> get().mapMarquePibi
        TypeNomenclature.MATERIAU -> get().mapMateriau
        TypeNomenclature.MODELE_PIBI -> get().mapModelePibi
        TypeNomenclature.NATURE -> get().mapNature
        TypeNomenclature.NATURE_DECI -> get().mapNatureDeci
        TypeNomenclature.NIVEAU -> get().mapNiveau
        TypeNomenclature.TYPE_CANALISATION -> get().mapTypeCanalisation
        TypeNomenclature.TYPE_RESEAU -> get().mapTypeReseau
    }

    /**
     * Fonction permettant de retourner la classe du POJO attendu en fonction du type (pour introspection)
     */
    fun getPojoClassFromType(typeNomenclature: TypeNomenclature) = when (typeNomenclature) {
        TypeNomenclature.DIAMETRE -> Diametre::class.java
        TypeNomenclature.DOMAINE -> Domaine::class.java
        TypeNomenclature.MARQUE_PIBI -> MarquePibi::class.java
        TypeNomenclature.MATERIAU -> Materiau::class.java
        TypeNomenclature.MODELE_PIBI -> ModelePibi::class.java
        TypeNomenclature.NATURE -> Nature::class.java
        TypeNomenclature.NATURE_DECI -> NatureDeci::class.java
        TypeNomenclature.NIVEAU -> Niveau::class.java
        TypeNomenclature.TYPE_CANALISATION -> TypeCanalisation::class.java
        TypeNomenclature.TYPE_RESEAU -> TypeReseau::class.java
    }
}
