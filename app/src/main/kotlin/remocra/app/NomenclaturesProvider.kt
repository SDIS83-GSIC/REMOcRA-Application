package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.NomenclaturesData
import remocra.data.enums.TypeNomenclature
import remocra.db.CommuneRepository
import remocra.db.DiametreRepository
import remocra.db.DomaineRepository
import remocra.db.MateriauRepository
import remocra.db.NatureDeciRepository
import remocra.db.NatureRepository
import remocra.db.TypeCanalisationRepository
import remocra.db.TypeReseauRepository

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
    private val materiauRepository: MateriauRepository,
    private val natureRepository: NatureRepository,
    private val natureDeciRepository: NatureDeciRepository,
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
            TypeNomenclature.MATERIAU -> nomenclaturesData.mapMateriau = materiauRepository.getMapById()
            TypeNomenclature.NATURE -> nomenclaturesData.mapNature = natureRepository.getMapById()
            TypeNomenclature.NATURE_DECI -> nomenclaturesData.mapNatureDeci = natureDeciRepository.getMapById()
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
        val materiaux = materiauRepository.getMapById()
        val nature = natureRepository.getMapById()
        val natureDeci = natureDeciRepository.getMapById()
        val typeCanalisation = typeCanalisationRepository.getMapById()
        val typeReseau = typeReseauRepository.getMapById()

        return NomenclaturesData(
            // mapCommune = communes,
            mapDiametre = diametres,
            mapDomaine = domaines,
            mapMateriau = materiaux,
            mapNature = nature,
            mapNatureDeci = natureDeci,
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
        TypeNomenclature.MATERIAU -> get().mapMateriau
        TypeNomenclature.NATURE -> get().mapNature
        TypeNomenclature.NATURE_DECI -> get().mapNatureDeci
        TypeNomenclature.TYPE_CANALISATION -> get().mapTypeCanalisation
        TypeNomenclature.TYPE_RESEAU -> get().mapTypeReseau
    }
}
