package remocra.app

import com.google.inject.Provider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import remocra.data.DataCache
import remocra.data.enums.TypeDataCache
import remocra.db.AnomalieRepository
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
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.pojos.Materiau
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Niveau
import remocra.db.jooq.remocra.tables.pojos.TypeCanalisation
import remocra.db.jooq.remocra.tables.pojos.TypeReseau

/**
 * Classe permettant de fournir toutes les données stockées en cache dans REMOcRA
 */
@Singleton
class DataCacheProvider
@Inject
constructor(
    private val anomalieRepository: AnomalieRepository,
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

) : Provider<DataCache> {
    private lateinit var dataCache: DataCache
    override fun get(): DataCache {
        if (!this::dataCache.isInitialized) {
            dataCache = buildDataCache()
        }
        return dataCache
    }

    /**
     * Permet de reconstruire le cache sur un type particulier (suite à la modification d'un élément).
     */
    fun reload(typeToReload: TypeDataCache) {
        when (typeToReload) {
            TypeDataCache.ANOMALIE -> dataCache.mapAnomalie = anomalieRepository.getMapById()
            TypeDataCache.DIAMETRE -> dataCache.mapDiametre = diametreRepository.getMapById()
            TypeDataCache.DOMAINE -> dataCache.mapDomaine = domaineRepository.getMapById()
            TypeDataCache.MARQUE_PIBI -> dataCache.mapMarquePibi = marquePibiRepository.getMapById()
            TypeDataCache.MATERIAU -> dataCache.mapMateriau = materiauRepository.getMapById()
            TypeDataCache.MODELE_PIBI -> dataCache.mapModelePibi = modelePibiRepository.getMapById()
            TypeDataCache.NATURE,
            TypeDataCache.NATURE_PENA,
            TypeDataCache.NATURE_PIBI,
            -> dataCache.mapNature = natureRepository.getMapById()
            TypeDataCache.NATURE_DECI -> dataCache.mapNatureDeci = natureDeciRepository.getMapById()
            TypeDataCache.NIVEAU -> dataCache.mapNiveau = niveauRepository.getMapById()
            TypeDataCache.TYPE_CANALISATION -> dataCache.mapTypeCanalisation = typeCanalisationRepository.getMapById()
            TypeDataCache.TYPE_RESEAU -> dataCache.mapTypeReseau = typeReseauRepository.getMapById()
        }
    }

    /**
     * Charge les données dans le cache pour utilisation ultérieure.
     */
    private fun buildDataCache(): DataCache {
        val anomalies = anomalieRepository.getMapById()
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

        return DataCache(
            mapAnomalie = anomalies,
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
    fun getData(typeDataCache: TypeDataCache) = when (typeDataCache) {
        TypeDataCache.ANOMALIE -> getAnomalies()
        TypeDataCache.DIAMETRE -> getDiametres()
        TypeDataCache.DOMAINE -> get().mapDomaine
        TypeDataCache.MARQUE_PIBI -> get().mapMarquePibi
        TypeDataCache.MATERIAU -> get().mapMateriau
        TypeDataCache.MODELE_PIBI -> get().mapModelePibi
        TypeDataCache.NATURE -> getNatures()
        TypeDataCache.NATURE_PIBI -> get().mapNature.filter { it.value.natureTypePei == TypePei.PIBI }
        TypeDataCache.NATURE_PENA -> get().mapNature.filter { it.value.natureTypePei == TypePei.PENA }
        TypeDataCache.NATURE_DECI -> get().mapNatureDeci
        TypeDataCache.NIVEAU -> get().mapNiveau
        TypeDataCache.TYPE_CANALISATION -> get().mapTypeCanalisation
        TypeDataCache.TYPE_RESEAU -> get().mapTypeReseau
    }

    fun getAnomalies() = get().mapAnomalie

    fun getDiametres() = get().mapDiametre

    fun getNatures() = get().mapNature

    /**
     * Fonction permettant de retourner la classe du POJO attendu en fonction du type (pour introspection)
     */
    fun getPojoClassFromType(typeDataCache: TypeDataCache) = when (typeDataCache) {
        TypeDataCache.ANOMALIE -> Anomalie::class.java
        TypeDataCache.DIAMETRE -> Diametre::class.java
        TypeDataCache.DOMAINE -> Domaine::class.java
        TypeDataCache.MARQUE_PIBI -> MarquePibi::class.java
        TypeDataCache.MATERIAU -> Materiau::class.java
        TypeDataCache.MODELE_PIBI -> ModelePibi::class.java
        TypeDataCache.NATURE,
        TypeDataCache.NATURE_PIBI,
        TypeDataCache.NATURE_PENA,
        -> Nature::class.java
        TypeDataCache.NATURE_DECI -> NatureDeci::class.java
        TypeDataCache.NIVEAU -> Niveau::class.java
        TypeDataCache.TYPE_CANALISATION -> TypeCanalisation::class.java
        TypeDataCache.TYPE_RESEAU -> TypeReseau::class.java
    }
}
