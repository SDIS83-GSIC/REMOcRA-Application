package remocra.usecase.modeleminimalpei

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import remocra.GlobalConstants
import remocra.api.usecase.AbstractApiPeiUseCase
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.ModeleMinimalPeiData
import remocra.data.ModeleMinimalPeiForNexsisData
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.db.VisiteRepository
import remocra.db.VoieRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypePeiNexsis
import remocra.db.jooq.remocra.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.pojos.Voie
import remocra.usecase.geometrie.GetCoordonneesBySrid
import remocra.utils.AdresseDecorator
import remocra.utils.AdresseForDecorator
import remocra.utils.DiametreDecorator
import java.util.Collections
import java.util.UUID

class GetModeleMinimalPeiUseCase @Inject
constructor(
    override val peiRepository: PeiRepository,
) : AbstractApiPeiUseCase(peiRepository) {

    @Inject
    lateinit var voieRepository: VoieRepository

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var tracabiliteRepository: TracabiliteRepository

    @Inject
    lateinit var diametreDecorator: DiametreDecorator

    @Inject
    lateinit var adresseDecorator: AdresseDecorator

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    /**
     * Récupère une liste de PEI au format minimal, avec accessibilité calculée
     * @param codeInsee filtre sur le code INSEE de la commune
     * @param type filtre sur le type de PEI
     * @param codeNature filtre sur la nature du PEI
     * @param codeNatureDECI filtre sur la nature DECI du PEI
     *  @param limit nombre maximum de PEI à retourner
     *  @param offset décalage pour la pagination
     *  @param wrappedUserInfo utilisateur effectuant la requête
     *  @param forNexsis si true, retourne ModeleMinimalPeiForNexsisData avec codeStructure
     */
    fun execute(
        codeInsee: String?,
        type: TypePei?,
        codeNature: String?,
        codeNatureDECI: String?,
        limit: Int?,
        offset: Int?,
        wrappedUserInfo: WrappedUserInfo,
        forNexsis: Boolean = false,
    ): Collection<ModeleMinimalPeiData> {
        val listePei = peiRepository.getListPeiForApi(codeInsee, type, codeNature, codeNatureDECI, limit, offset)

        // Une seule requête pour calculer leur accessibilité, on se servira de la map<id, POJO> par la suite
        val mapAccessibilite = listPeiAccessibilite(listePei.map { it.peiId }.toSet(), wrappedUserInfo).associateBy { it.id }

        return getModeleMinimalPei(listePei.filter { p -> mapAccessibilite[p.peiId] != null && mapAccessibilite[p.peiId]!!.isAccessible }, forNexsis)
    }

    fun execute(peiId: UUID, forNexsis: Boolean = false): ModeleMinimalPeiData {
        return getModeleMinimalPei(Collections.singletonList(peiRepository.getPeiForApi(peiId)), forNexsis).first()
    }

    /**
     * Retourne une liste de PEI au format minimal attendu par NexSIS (correspond à quelques libertés près au modèle minimal de l'Afigéo)
     */
    private fun getModeleMinimalPei(listePei: Collection<PeiRepository.PeiDataForApi>, forNexsis: Boolean = false): Collection<ModeleMinimalPeiData> {
        val listeVoie = voieRepository.getAll()

        return listePei.map {
            val lastCtrl = visiteRepository.getLastVisiteDebitPression(it.peiId)

            val dateMiseAjour = tracabiliteRepository.getDateDernierChangementPei(it.peiId)

            // On recalcule la géométrie en 4326 (attendu NexSIS)
            val geom = getCoordonneesBySrid.execute(it.peiGeometrie.x.toString(), it.peiGeometrie.y.toString(), appSettings.srid)
                .find { it.srid == GlobalConstants.SRID_4326 }

            if (forNexsis) {
                ModeleMinimalPeiForNexsisData(
                    codeStructure = appSettings.nexsis.codeStructure!!,
                    peiId = getPeiId(it),
                    peiNumeroComplet = getPeiNumeroComplet(it),
                    typePeiNexSis = getTypePeiNexsis(it),
                    isDisponible = getIsDisponible(it),
                    geometrie = getGeometrie(geom!!),
                    codeInsee = getCodeInsee(it),
                    communeLibelle = getCommuneLibelle(it),
                    idGestion = getIdGestion(),
                    nomGest = getNomGest(it),
                    peiNumeroInterne = getPeiNumeroInterne(it),
                    typeRD = getTypeRD(),
                    diametre = getDiametre(it),
                    pibiDiametreCanalisation = getPibiDiametreCanalisation(it),
                    natureLibelle = getNatureLibelle(it),
                    natureDeci = getNatureDeci(it),
                    site = getSite(it),
                    adresse = getAdresse(it, listeVoie),
                    pibiPressionDynamique = getPibiPressionDynamique(lastCtrl),
                    pibiPression = getPibiPression(lastCtrl),
                    pibiDebit = getPibiDebit(lastCtrl),
                    penaVolumeConstate = getPenaVolumeConstate(it),
                    instantChangementDispo = it.peiDateChangementDispo,
                    dateMiseEnService = it.lastRecoInit,
                    dateMiseAJour = dateMiseAjour,
                    dateDernierControleTechnique = it.lastCtp,
                    dateDerniereRop = it.lastRop,
                    precision = getPrecision(),
                    isNonConforme = getIsNonConforme(it),
                    isAccessibleHbe = getIsAccessibleHbe(it),
                )
            } else {
                ModeleMinimalPeiData(
                    peiId = getPeiId(it),
                    peiNumeroComplet = getPeiNumeroComplet(it),
                    typePeiNexSis = getTypePeiNexsis(it),
                    isDisponible = getIsDisponible(it),
                    geometrie = getGeometrie(geom!!),
                    codeInsee = getCodeInsee(it),
                    communeLibelle = getCommuneLibelle(it),
                    idGestion = getIdGestion(),
                    nomGest = getNomGest(it),
                    peiNumeroInterne = getPeiNumeroInterne(it),
                    typeRD = getTypeRD(),
                    diametre = getDiametre(it),
                    pibiDiametreCanalisation = getPibiDiametreCanalisation(it),
                    natureLibelle = getNatureLibelle(it),
                    natureDeci = getNatureDeci(it),
                    site = getSite(it),
                    adresse = getAdresse(it, listeVoie),
                    pibiPressionDynamique = getPibiPressionDynamique(lastCtrl),
                    pibiPression = getPibiPression(lastCtrl),
                    pibiDebit = getPibiDebit(lastCtrl),
                    penaVolumeConstate = getPenaVolumeConstate(it),
                    instantChangementDispo = it.peiDateChangementDispo,
                    dateMiseEnService = it.lastRecoInit,
                    dateMiseAJour = dateMiseAjour,
                    dateDernierControleTechnique = it.lastCtp,
                    dateDerniereRop = it.lastRop,
                    precision = getPrecision(),
                    isNonConforme = getIsNonConforme(it),
                    isAccessibleHbe = getIsAccessibleHbe(it),
                ) }
        }
    }

    private fun getPeiId(it: PeiRepository.PeiDataForApi): UUID = it.peiId

    private fun getPeiNumeroComplet(it: PeiRepository.PeiDataForApi): String = it.peiNumeroComplet

    private fun getTypePeiNexsis(it: PeiRepository.PeiDataForApi): TypePeiNexsis? = it.natureTypePeiNexsis

    private fun getIsDisponible(it: PeiRepository.PeiDataForApi): Boolean =
        it.peiDisponibiliteTerrestre == Disponibilite.DISPONIBLE || it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME

    private fun getGeometrie(geom: GetCoordonneesBySrid.CoordonneesBySysteme): Point =
        GeometryFactory(PrecisionModel(), GlobalConstants.SRID_4326).createPoint(
            Coordinate(geom.coordonneeX.toDouble(), geom.coordonneeY.toDouble()),
        )

    private fun getCodeInsee(it: PeiRepository.PeiDataForApi): String = it.communeCodeInsee

    private fun getCommuneLibelle(it: PeiRepository.PeiDataForApi): String = it.communeLibelle

    private fun getIdGestion(): String? = null

    private fun getNomGest(it: PeiRepository.PeiDataForApi): String? = it.serviceEauLibelle

    private fun getPeiNumeroInterne(it: PeiRepository.PeiDataForApi): String = it.peiNumeroInterne

    private fun getTypeRD(): String? = null

    private fun getDiametre(it: PeiRepository.PeiDataForApi): Int? =
        it.diametreCode?.let { diametreDecorator.decorateDiametre(it) }

    private fun getPibiDiametreCanalisation(it: PeiRepository.PeiDataForApi): Int? = it.pibiDiametreCanalisation

    private fun getNatureLibelle(it: PeiRepository.PeiDataForApi): String = it.natureLibelle

    private fun getNatureDeci(it: PeiRepository.PeiDataForApi): String = it.natureDeciLibelle

    private fun getSite(it: PeiRepository.PeiDataForApi): String? = it.siteLibelle

    private fun getAdresse(it: PeiRepository.PeiDataForApi, listeVoie: Collection<Voie>): String =
        adresseDecorator.decorateAdresse(
            AdresseForDecorator(
                enFace = it.peiEnFace,
                numeroVoie = it.peiNumeroVoie,
                suffixeVoie = it.peiSuffixeVoie,
                voie = listeVoie.firstOrNull { v -> it.peiVoieId == v.voieId },
                voieTexte = it.peiVoieTexte,
            ),
        )

    private fun getPibiPressionDynamique(lastCtrl: VisiteCtrlDebitPression?): Double? =
        lastCtrl?.visiteCtrlDebitPressionPressionDyn?.toDouble()

    private fun getPibiPression(lastCtrl: VisiteCtrlDebitPression?): Double? =
        lastCtrl?.visiteCtrlDebitPressionPression?.toDouble()

    private fun getPibiDebit(lastCtrl: VisiteCtrlDebitPression?): Int? = lastCtrl?.visiteCtrlDebitPressionDebit

    private fun getPenaVolumeConstate(it: PeiRepository.PeiDataForApi): Int? = it.penaCapacite

    private fun getPrecision(): String? = null

    private fun getIsNonConforme(it: PeiRepository.PeiDataForApi): Boolean =
        it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME

    private fun getIsAccessibleHbe(it: PeiRepository.PeiDataForApi): Boolean =
        it.penaDisponibiliteHbe == Disponibilite.DISPONIBLE
}
