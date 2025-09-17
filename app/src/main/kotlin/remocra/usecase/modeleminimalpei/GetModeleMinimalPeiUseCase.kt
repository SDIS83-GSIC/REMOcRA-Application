package remocra.usecase.modeleminimalpei

import jakarta.inject.Inject
import org.locationtech.jts.io.geojson.GeoJsonWriter
import remocra.api.usecase.AbstractApiPeiUseCase
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.ModeleMinimalPeiData
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.db.VisiteRepository
import remocra.db.VoieRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
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
            val lastCtrl = listeCtrlDebitPression.filter { v -> v.visitePeiId == it.peiId && v.isCtrlDebitPression }
                .maxByOrNull { it.visiteDate }

            ModeleMinimalPeiData(
                codeStructure = appSettings.nexsis.codeStructure!!,
                peiId = it.peiId,
                peiNumeroComplet = it.peiNumeroComplet,
                natureCode = it.natureCode,
                isDisponible = it.peiDisponibiliteTerrestre == Disponibilite.DISPONIBLE || it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME,
                geometrie = GeoJsonWriter().write(it.peiGeometrie),
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
                dateMiseEnService = dateUtils.formatDateOnly(it.lastRecoInit),
                dateMiseAJour = dateUtils.formatDateOnly(mapDateDerniereModif[it.peiId]),
                dateDernierControleTechnique = dateUtils.formatDateOnly(it.lastCtp),
                dateDerniereRop = dateUtils.formatDateOnly(it.lastRop),
                precision = null,
                isNonConforme = it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME,
                isAccessibleHbe = it.penaDisponibiliteHbe == Disponibilite.DISPONIBLE,
            )
        }
    }
}
