package remocra.usecase.modeleminimalpei

import jakarta.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
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
    lateinit var getCoordonneesBySrid: GetCoordonneesBySrid

    fun execute(
        codeInsee: String?,
        type: TypePei?,
        codeNature: String?,
        codeNatureDECI: String?,
        limit: Int?,
        offset: Int?,
        wrappedUserInfo: WrappedUserInfo,
    ): Collection<ModeleMinimalPeiData> {
        val listePei = peiRepository.getListPeiForApi(codeInsee, type, codeNature, codeNatureDECI, limit, offset)

        // Une seule requête pour calculer leur accessibilité, on se servira de la map<id, POJO> par la suite
        val mapAccessibilite = listPeiAccessibilite(listePei.map { it.peiId }.toSet(), wrappedUserInfo).associateBy { it.id }

        return getModeleMinimalPei(listePei.filter { p -> mapAccessibilite[p.peiId] != null && mapAccessibilite[p.peiId]!!.isAccessible })
    }

    fun execute(peiId: UUID): ModeleMinimalPeiData {
        return getModeleMinimalPei(Collections.singletonList(peiRepository.getPeiForApi(peiId))).first()
    }

    private fun getModeleMinimalPei(listePei: Collection<PeiRepository.PeiDataForApi>): Collection<ModeleMinimalPeiData> {
        val listeVoie = voieRepository.getAll()

        val listePeiId = listePei.map { it.peiId }

        val listeCtrlDebitPression = visiteRepository.getAllVisiteByIdPei(listePeiId)
        val mapDateDerniereModif = tracabiliteRepository.getLastDateByPei(listePeiId)

        return listePei.map {
            // On recalcule la géométrie en 4326 (attendu NexSIS)
            val geom = getCoordonneesBySrid.execute(it.peiGeometrie.x.toString(), it.peiGeometrie.y.toString(), appSettings.srid)
                .find { it.srid == GlobalConstants.SRID_4326 }

            // Et on écrit un Point qui sera sérialisé proprement par la suite
            val peiGeometrie =
                GeometryFactory(PrecisionModel(), GlobalConstants.SRID_4326).createPoint(
                    Coordinate(
                        geom!!.coordonneeX.toDouble(),
                        geom.coordonneeY.toDouble(),
                    ),
                )

            val lastCtrl = listeCtrlDebitPression.filter { v -> v.visitePeiId == it.peiId && v.isCtrlDebitPression }
                .maxByOrNull { it.visiteDate }

            ModeleMinimalPeiForNexsisData(
                codeStructure = appSettings.nexsis.codeStructure!!,
                peiId = it.peiId,
                peiNumeroComplet = it.peiNumeroComplet,
                natureCode = it.natureCode,
                isDisponible = it.peiDisponibiliteTerrestre == Disponibilite.DISPONIBLE || it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME,
                geometrie = peiGeometrie,
                codeInsee = it.communeCodeInsee,
                communeLibelle = it.communeLibelle,
                idGestion = null,
                nomGest = it.serviceEauLibelle,
                peiNumeroInterne = it.peiNumeroInterne,
                typeRD = null,
                diametre = it.diametreCode?.let { diametreDecorator.decorateDiametre(it) },
                pibiDiametreCanalisation = it.pibiDiametreCanalisation,
                natureLibelle = it.natureLibelle,
                natureDeci = it.natureDeciLibelle,
                site = it.siteLibelle,
                adresse = adresseDecorator.decorateAdresse(
                    AdresseForDecorator(
                        enFace = it.peiEnFace,
                        numeroVoie = it.peiNumeroVoie,
                        suffixeVoie = it.peiSuffixeVoie,
                        voie = listeVoie.firstOrNull { v -> it.peiVoieId == v.voieId },
                        voieTexte = it.peiVoieTexte,
                    ),
                ),
                pibiPressionDynamique = lastCtrl?.ctrlDebitPression?.visiteCtrlDebitPressionPressionDyn?.toDouble(),
                pibiPression = lastCtrl?.ctrlDebitPression?.visiteCtrlDebitPressionPression?.toDouble(),
                pibiDebit = lastCtrl?.ctrlDebitPression?.visiteCtrlDebitPressionDebit,
                penaVolumeConstate = it.penaCapacite,
                instantChangementDispo = null,
                dateMiseEnService = it.lastRecoInit,
                dateMiseAJour = mapDateDerniereModif[it.peiId],
                dateDernierControleTechnique = it.lastCtp,
                dateDerniereRop = it.lastRop,
                precision = null,
                isNonConforme = it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME,
                isAccessibleHbe = it.penaDisponibiliteHbe == Disponibilite.DISPONIBLE,
            )
        }
    }
}
