package remocra.app

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.inject.Provider
import jakarta.inject.Singleton
import remocra.data.CoucheData
import remocra.data.DataCache
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.enums.TypeDataCache
import remocra.db.AnomalieRepository
import remocra.db.CoucheRepository
import remocra.db.DiametreRepository
import remocra.db.DomaineRepository
import remocra.db.EvenementCategorieRepository
import remocra.db.MarquePibiRepository
import remocra.db.MateriauRepository
import remocra.db.ModelePibiRepository
import remocra.db.NatureDeciRepository
import remocra.db.NatureRepository
import remocra.db.NiveauRepository
import remocra.db.OldebRepository
import remocra.db.RcciIndiceRothermelRepository
import remocra.db.RcciRepository
import remocra.db.ReservoirRepository
import remocra.db.TypeCanalisationRepository
import remocra.db.TypeCriseRepository
import remocra.db.TypeEnginRepository
import remocra.db.TypeOrganismeRepository
import remocra.db.TypeReseauRepository
import remocra.db.UtilisateurRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.EvenementCategorie
import remocra.db.jooq.remocra.tables.pojos.MarquePibi
import remocra.db.jooq.remocra.tables.pojos.Materiau
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Niveau
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAcces
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAction
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeAvis
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieAnomalie
import remocra.db.jooq.remocra.tables.pojos.OldebTypeCategorieCaracteristique
import remocra.db.jooq.remocra.tables.pojos.OldebTypeDebroussaillement
import remocra.db.jooq.remocra.tables.pojos.OldebTypeResidence
import remocra.db.jooq.remocra.tables.pojos.OldebTypeSuite
import remocra.db.jooq.remocra.tables.pojos.OldebTypeZoneUrbanisme
import remocra.db.jooq.remocra.tables.pojos.RcciIndiceRothermel
import remocra.db.jooq.remocra.tables.pojos.RcciTypeDegreCertitude
import remocra.db.jooq.remocra.tables.pojos.RcciTypeOrigineAlerte
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeCategorie
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheeFamille
import remocra.db.jooq.remocra.tables.pojos.RcciTypePrometheePartition
import remocra.db.jooq.remocra.tables.pojos.Reservoir
import remocra.db.jooq.remocra.tables.pojos.TypeCanalisation
import remocra.db.jooq.remocra.tables.pojos.TypeCrise
import remocra.db.jooq.remocra.tables.pojos.TypeEngin
import remocra.db.jooq.remocra.tables.pojos.TypeOrganisme
import remocra.db.jooq.remocra.tables.pojos.TypeReseau
import remocra.eventbus.EventListener
import remocra.eventbus.datacache.DataCacheModifiedEvent

/**
 * Classe permettant de fournir toutes les données stockées en cache dans REMOcRA
 */
@Singleton
class DataCacheProvider
@Inject
constructor(
    private val anomalieRepository: AnomalieRepository,
    private val coucheRepository: CoucheRepository,
    private val evenementCategorieRepository: EvenementCategorieRepository,
    private val diametreRepository: DiametreRepository,
    private val domaineRepository: DomaineRepository,
    private val marquePibiRepository: MarquePibiRepository,
    private val materiauRepository: MateriauRepository,
    private val modelePibiRepository: ModelePibiRepository,
    private val natureRepository: NatureRepository,
    private val natureDeciRepository: NatureDeciRepository,
    private val niveauRepository: NiveauRepository,
    private val oldebRepository: OldebRepository,
    private val rcciRepository: RcciRepository,
    private val rcciIndiceRothermelRepository: RcciIndiceRothermelRepository,
    private val reservoirRepository: ReservoirRepository,
    private val typeCanalisationRepository: TypeCanalisationRepository,
    private val typeCriseRepository: TypeCriseRepository,
    private val typeOrganismeRepository: TypeOrganismeRepository,
    private val typeReseauRepository: TypeReseauRepository,
    private val typeEnginRepository: TypeEnginRepository,
    private val utilisateurRepository: UtilisateurRepository,
    // private val nomenclatureCodeLibelleRepository: NomenclatureCodeLibelleRepository,

) : Provider<DataCache>, EventListener<DataCacheModifiedEvent> {
    private lateinit var dataCache: DataCache
    override fun get(): DataCache {
        if (!this::dataCache.isInitialized) {
            dataCache = buildDataCache()
        }
        return dataCache
    }

    @Subscribe
    override fun onEvent(event: DataCacheModifiedEvent) {
        reload(event.typeDataCache)
    }

    /**
     * Permet de reconstruire le cache sur un type particulier (suite à la modification d'un élément).
     */
    fun reload(typeToReload: TypeDataCache) {
        when (typeToReload) {
            TypeDataCache.ANOMALIE -> dataCache.mapAnomalie = anomalieRepository.getMapById()
            TypeDataCache.ANOMALIE_CATEGORIE -> dataCache.mapAnomalieCategorie = anomalieRepository.getAnomalieCategorie().associateBy { it.anomalieCategorieId }
            TypeDataCache.COUCHE -> dataCache.mapCouches = coucheRepository.getMapById()
            TypeDataCache.EVENEMENT_CATEGORIE -> dataCache.mapEvenementCategorie = evenementCategorieRepository.getMapById()
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
            TypeDataCache.OLDEB_TYPE_ACTION -> dataCache.mapOldebTypeAction = oldebRepository.getTypeAction()
            TypeDataCache.OLDEB_TYPE_AVIS -> dataCache.mapOldebTypeAvis = oldebRepository.getTypeAvisMap()
            TypeDataCache.OLDEB_TYPE_DEBROUSSAILLEMENT -> dataCache.mapOldebTypeDebrousaillement = oldebRepository.getTypeDebroussaillementMap()
            TypeDataCache.OLDEB_TYPE_ANOMALIE -> dataCache.mapOldebTypeAnomalie = oldebRepository.getTypeAnomalie()
            TypeDataCache.OLDEB_TYPE_CATEGORIE_ANOMALIE -> dataCache.mapOldebTypeCategorieAnomalie = oldebRepository.getTypeCategorieAnomalie()
            TypeDataCache.OLDEB_TYPE_ACCES -> dataCache.mapOldebTypeAcces = oldebRepository.getTypeAcces()
            TypeDataCache.OLDEB_TYPE_RESIDENCE -> dataCache.mapOldebTypeResidence = oldebRepository.getTypeResidence()
            TypeDataCache.OLDEB_TYPE_SUITE -> dataCache.mapOldebTypeSuite = oldebRepository.getTypeSuite()
            TypeDataCache.OLDEB_TYPE_ZONE_URBANISME -> dataCache.mapOldebTypeZoneUrbanisme = oldebRepository.getTypeZoneUrbanismeMap()
            TypeDataCache.OLDEB_TYPE_CARACTERISTIQUE -> dataCache.mapOldebTypeCaracteristique = oldebRepository.getTypeCaracteristiqueMap()
            TypeDataCache.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE -> dataCache.mapOldebTypeCategorieCaracteristique = oldebRepository.getTypeCategorieCaracteristiqueMap()
            TypeDataCache.RCCI_INDICE_ROTHERMEL -> dataCache.mapRcciIndiceRothermel = rcciIndiceRothermelRepository.getMapById()
            TypeDataCache.RCCI_TYPE_DEGRE_CERTITUDE -> dataCache.mapRcciTypeDegreCertitude = rcciRepository.getMapTypeDegreCertitude()
            TypeDataCache.RCCI_TYPE_ORIGINE_ALERTE -> dataCache.mapRcciTypeOrigineAlerte = rcciRepository.getMapTypeOrigineAlerte()
            TypeDataCache.RCCI_TYPE_PROMETHEE_CATEGORIE -> dataCache.mapRcciTypePrometheeCategorie = rcciRepository.getMapTypePrometheeCategorie()
            TypeDataCache.RCCI_TYPE_PROMETHEE_FAMILLE -> dataCache.mapRcciTypePrometheeFamille = rcciRepository.getMapTypePrometheeFamille()
            TypeDataCache.RCCI_TYPE_PROMETHEE_PARTITION -> dataCache.mapRcciTypePrometheePartition = rcciRepository.getMapTypePrometheePartition()
            TypeDataCache.RESERVOIR -> dataCache.mapReservoir = reservoirRepository.getMapById()
            TypeDataCache.TYPE_CANALISATION -> dataCache.mapTypeCanalisation = typeCanalisationRepository.getMapById()
            TypeDataCache.TYPE_CRISE -> dataCache.mapTypeCrise = typeCriseRepository.getMapById()
            TypeDataCache.TYPE_ENGIN -> dataCache.mapTypeEngin = typeEnginRepository.getMapById()
            TypeDataCache.TYPE_ORGANISME -> dataCache.mapTypeOrganisme = typeOrganismeRepository.getMapById()
            TypeDataCache.TYPE_RESEAU -> dataCache.mapTypeReseau = typeReseauRepository.getMapById()
        }
    }

    /**
     * Charge les données dans le cache pour utilisation ultérieure.
     */
    private fun buildDataCache(): DataCache {
        val anomalies = anomalieRepository.getMapById()
        val anomaliesCategories = anomalieRepository.getAnomalieCategorie().associateBy { it.anomalieCategorieId }
        val couches = coucheRepository.getMapById()
        val criseCategorie = evenementCategorieRepository.getMapById()
        val diametres = diametreRepository.getMapById()
        val domaines = domaineRepository.getMapById()
        val marquesPibi = marquePibiRepository.getMapById()
        val materiaux = materiauRepository.getMapById()
        val modelesPibi = modelePibiRepository.getMapById()
        val nature = natureRepository.getMapById()
        val natureDeci = natureDeciRepository.getMapById()
        val niveau = niveauRepository.getMapById()
        val oldebTypeAcces = oldebRepository.getTypeAcces()
        val oldebTypeAction = oldebRepository.getTypeAction()
        val oldebTypeAnomalie = oldebRepository.getTypeAnomalie()
        val oldebTypeAvis = oldebRepository.getTypeAvisMap()
        val oldebTypeCaracteristique = oldebRepository.getTypeCaracteristiqueMap()
        val oldebTypeCategorieAnomalie = oldebRepository.getTypeCategorieAnomalie()
        val oldebTypeCategorieCaracteristique = oldebRepository.getTypeCategorieCaracteristiqueMap()
        val oldebTypeDebrousaillement = oldebRepository.getTypeDebroussaillementMap()
        val oldebTypeResidence = oldebRepository.getTypeResidence()
        val oldebTypeSuite = oldebRepository.getTypeSuite()
        val oldebTypeZoneUrbanisme = oldebRepository.getTypeZoneUrbanismeMap()
        val mapIndiceRothermel = rcciIndiceRothermelRepository.getMapById()
        val mapRcciTypeDegreCertitude = rcciRepository.getMapTypeDegreCertitude()
        val mapRcciTypeOrigineAlerte = rcciRepository.getMapTypeOrigineAlerte()
        val mapRcciTypePrometheeCategorie = rcciRepository.getMapTypePrometheeCategorie()
        val mapRcciTypePrometheeFamille = rcciRepository.getMapTypePrometheeFamille()
        val mapRcciTypePrometheePartition = rcciRepository.getMapTypePrometheePartition()
        val reservoir = reservoirRepository.getMapById()
        val typeCanalisation = typeCanalisationRepository.getMapById()
        val typeCrise = typeCriseRepository.getMapById()
        val typeEngin = typeEnginRepository.getMapById()
        val typeOrganisme = typeOrganismeRepository.getMapById()
        val typeReseau = typeReseauRepository.getMapById()
        val utilisateurSysteme = utilisateurRepository.getUtilisateurSysteme()

        return DataCache(
            mapAnomalie = anomalies,
            mapAnomalieCategorie = anomaliesCategories,
            mapCouches = couches,
            mapEvenementCategorie = criseCategorie,
            mapDiametre = diametres,
            mapDomaine = domaines,
            mapMateriau = materiaux,
            mapMarquePibi = marquesPibi,
            mapModelePibi = modelesPibi,
            mapNature = nature,
            mapNatureDeci = natureDeci,
            mapNiveau = niveau,
            mapOldebTypeAcces = oldebTypeAcces,
            mapOldebTypeAction = oldebTypeAction,
            mapOldebTypeAnomalie = oldebTypeAnomalie,
            mapOldebTypeAvis = oldebTypeAvis,
            mapOldebTypeCategorieAnomalie = oldebTypeCategorieAnomalie,
            mapOldebTypeCategorieCaracteristique = oldebTypeCategorieCaracteristique,
            mapOldebTypeCaracteristique = oldebTypeCaracteristique,
            mapOldebTypeDebrousaillement = oldebTypeDebrousaillement,
            mapOldebTypeResidence = oldebTypeResidence,
            mapOldebTypeSuite = oldebTypeSuite,
            mapOldebTypeZoneUrbanisme = oldebTypeZoneUrbanisme,
            mapRcciIndiceRothermel = mapIndiceRothermel,
            mapRcciTypeDegreCertitude = mapRcciTypeDegreCertitude,
            mapRcciTypeOrigineAlerte = mapRcciTypeOrigineAlerte,
            mapRcciTypePrometheeCategorie = mapRcciTypePrometheeCategorie,
            mapRcciTypePrometheeFamille = mapRcciTypePrometheeFamille,
            mapRcciTypePrometheePartition = mapRcciTypePrometheePartition,
            mapReservoir = reservoir,
            mapTypeCanalisation = typeCanalisation,
            mapTypeCrise = typeCrise,
            mapTypeEngin = typeEngin,
            mapTypeOrganisme = typeOrganisme,
            mapTypeReseau = typeReseau,
            utilisateurSysteme = utilisateurSysteme,
        )
    }

    /**
     * Permet de retourner une map de nomenclature en fonction de son type.
     */
    fun getData(typeDataCache: TypeDataCache) = when (typeDataCache) {
        TypeDataCache.ANOMALIE -> getAnomalies()
        TypeDataCache.ANOMALIE_CATEGORIE -> getAnomaliesCategories()
        TypeDataCache.COUCHE -> get().mapCouches
        TypeDataCache.EVENEMENT_CATEGORIE -> get().mapEvenementCategorie
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
        TypeDataCache.OLDEB_TYPE_ACCES -> get().mapOldebTypeAcces
        TypeDataCache.OLDEB_TYPE_ACTION -> get().mapOldebTypeAction
        TypeDataCache.OLDEB_TYPE_ANOMALIE -> get().mapOldebTypeAnomalie
        TypeDataCache.OLDEB_TYPE_AVIS -> get().mapOldebTypeAvis
        TypeDataCache.OLDEB_TYPE_CARACTERISTIQUE -> get().mapOldebTypeCaracteristique
        TypeDataCache.OLDEB_TYPE_CATEGORIE_ANOMALIE -> get().mapOldebTypeCategorieAnomalie
        TypeDataCache.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE -> get().mapOldebTypeCategorieCaracteristique
        TypeDataCache.OLDEB_TYPE_DEBROUSSAILLEMENT -> get().mapOldebTypeDebrousaillement
        TypeDataCache.OLDEB_TYPE_RESIDENCE -> get().mapOldebTypeResidence
        TypeDataCache.OLDEB_TYPE_SUITE -> get().mapOldebTypeSuite
        TypeDataCache.OLDEB_TYPE_ZONE_URBANISME -> get().mapOldebTypeZoneUrbanisme
        TypeDataCache.RCCI_INDICE_ROTHERMEL -> get().mapRcciIndiceRothermel
        TypeDataCache.RCCI_TYPE_DEGRE_CERTITUDE -> getMapRcciTypeDegreCertitude()
        TypeDataCache.RCCI_TYPE_ORIGINE_ALERTE -> getMapRcciTypeOrigineAlerte()
        TypeDataCache.RCCI_TYPE_PROMETHEE_CATEGORIE -> getMapRcciTypePrometheeCategorie()
        TypeDataCache.RCCI_TYPE_PROMETHEE_FAMILLE -> getMapRcciTypePrometheeFamille()
        TypeDataCache.RCCI_TYPE_PROMETHEE_PARTITION -> getMapRcciTypePrometheePartition()
        TypeDataCache.RESERVOIR -> getReservoirs()
        TypeDataCache.TYPE_CANALISATION -> get().mapTypeCanalisation
        TypeDataCache.TYPE_CRISE -> get().mapTypeCrise
        TypeDataCache.TYPE_ENGIN -> get().mapTypeEngin
        TypeDataCache.TYPE_ORGANISME -> get().mapTypeOrganisme
        TypeDataCache.TYPE_RESEAU -> get().mapTypeReseau
    }

    fun getAnomalies() = get().mapAnomalie

    fun getAnomaliesCategories() = get().mapAnomalieCategorie

    fun getMapRcciTypePrometheeFamille() = get().mapRcciTypePrometheeFamille

    fun getMapRcciTypePrometheePartition() = get().mapRcciTypePrometheePartition

    fun getMapRcciTypePrometheeCategorie() = get().mapRcciTypePrometheeCategorie

    fun getMapRcciTypeOrigineAlerte() = get().mapRcciTypeOrigineAlerte

    fun getMapRcciTypeDegreCertitude() = get().mapRcciTypeDegreCertitude

    fun getDiametres() = get().mapDiametre

    fun getNatures() = get().mapNature

    fun getDomaines() = get().mapDomaine

    fun getNaturesDeci() = get().mapNatureDeci

    fun getMarquesPibi() = get().mapMarquePibi

    fun getModelesPibi() = get().mapModelePibi

    fun getReservoirs() = get().mapReservoir

    fun getTypesReseau() = get().mapTypeReseau

    fun getTypesCanalisation() = get().mapTypeCanalisation

    fun getTypesOrganisme() = get().mapTypeOrganisme

    fun getMateriaux() = get().mapMateriau

    /**
     * Fonction permettant de retourner la classe du POJO attendu en fonction du type (pour introspection)
     */
    fun getPojoClassFromType(typeDataCache: TypeDataCache) = when (typeDataCache) {
        TypeDataCache.ANOMALIE -> Anomalie::class.java
        TypeDataCache.ANOMALIE_CATEGORIE -> NomenclatureCodeLibelleData::class.java
        TypeDataCache.COUCHE -> CoucheData::class.java
        TypeDataCache.EVENEMENT_CATEGORIE -> EvenementCategorie::class.java
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
        TypeDataCache.OLDEB_TYPE_ACCES -> OldebTypeAcces::class.java
        TypeDataCache.OLDEB_TYPE_ACTION -> OldebTypeAction::class.java
        TypeDataCache.OLDEB_TYPE_ANOMALIE -> OldebTypeAnomalie::class.java
        TypeDataCache.OLDEB_TYPE_AVIS -> OldebTypeAvis::class.java
        TypeDataCache.OLDEB_TYPE_CARACTERISTIQUE -> OldebTypeCaracteristique::class.java
        TypeDataCache.OLDEB_TYPE_CATEGORIE_ANOMALIE -> OldebTypeCategorieAnomalie::class.java
        TypeDataCache.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE -> OldebTypeCategorieCaracteristique::class.java
        TypeDataCache.OLDEB_TYPE_DEBROUSSAILLEMENT -> OldebTypeDebroussaillement::class.java
        TypeDataCache.OLDEB_TYPE_RESIDENCE -> OldebTypeResidence::class.java
        TypeDataCache.OLDEB_TYPE_SUITE -> OldebTypeSuite::class.java
        TypeDataCache.OLDEB_TYPE_ZONE_URBANISME -> OldebTypeZoneUrbanisme::class.java
        TypeDataCache.RCCI_INDICE_ROTHERMEL -> RcciIndiceRothermel::class.java
        TypeDataCache.RCCI_TYPE_DEGRE_CERTITUDE -> RcciTypeDegreCertitude::class.java
        TypeDataCache.RCCI_TYPE_ORIGINE_ALERTE -> RcciTypeOrigineAlerte::class.java
        TypeDataCache.RCCI_TYPE_PROMETHEE_CATEGORIE -> RcciTypePrometheeCategorie::class.java
        TypeDataCache.RCCI_TYPE_PROMETHEE_FAMILLE -> RcciTypePrometheeFamille::class.java
        TypeDataCache.RCCI_TYPE_PROMETHEE_PARTITION -> RcciTypePrometheePartition::class.java
        TypeDataCache.RESERVOIR -> Reservoir::class.java
        TypeDataCache.TYPE_CANALISATION -> TypeCanalisation::class.java
        TypeDataCache.TYPE_CRISE -> TypeCrise::class.java
        TypeDataCache.TYPE_ENGIN -> TypeEngin::class.java
        TypeDataCache.TYPE_ORGANISME -> TypeOrganisme::class.java
        TypeDataCache.TYPE_RESEAU -> TypeReseau::class.java
    }

    /**
     * Fonction permettant de retourner la classe liée à un POJO
     */
    fun getLinkedPojoClassFromType(typeDataCache: TypeDataCache) = when (typeDataCache) {
        TypeDataCache.RCCI_TYPE_PROMETHEE_PARTITION -> RcciTypePrometheeFamille::class.java
        TypeDataCache.RCCI_TYPE_PROMETHEE_CATEGORIE -> RcciTypePrometheePartition::class.java
        else -> null
    }
}
