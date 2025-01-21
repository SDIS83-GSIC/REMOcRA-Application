package remocra.usecase.modeleminimalpei

import jakarta.inject.Inject
import org.locationtech.jts.io.geojson.GeoJsonWriter
import remocra.data.ModeleMinimalPeiData
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.db.VisiteRepository
import remocra.db.VoieRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase
import remocra.utils.AdresseDecorator
import remocra.utils.AdresseForDecorator
import remocra.utils.DiametreDecorator

class GetModeleMinimalPeiUseCase : AbstractUseCase() {

    @Inject
    lateinit var peiRepository: PeiRepository

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

    fun execute(
        codeInsee: String?,
        type: TypePei?,
        codeNature: String?,
        codeNatureDECI: String?,
        limit: Int?,
        offset: Int?,
    ): Collection<ModeleMinimalPeiData> {
        // TODO Accessibilité de chaque PEI ; soit dans la requête, soit en post-traitement avec le getPeiAccessibilite
        val listePei = peiRepository.getListPeiForApi(codeInsee, type, codeNature, codeNatureDECI, limit, offset)
        return getModeleMinimalPei(listePei)
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
                        complementAdresse = it.peiComplementAdresse,
                    ),
                ),
                pibiPressionDynamique = lastCtrl?.ctrlDebitPression?.ctrlPressionDyn?.toDouble(),
                pibiPression = lastCtrl?.ctrlDebitPression?.ctrlPression?.toDouble(),
                pibiDebit = lastCtrl?.ctrlDebitPression?.ctrlDebit,
                penaVolumeConstate = it.penaCapacite,
                instantChangementDispo = null,
                dateMiseEnService = dateUtils.formatDateOnly(it.lastRecoInit),
                dateMiseAJour = dateUtils.formatDateOnly(mapDateDerniereModif[it.peiId]),
                dateDernierControleTechnique = dateUtils.formatDateOnly(it.lastCtp),
                dateDerniereRecop = dateUtils.formatDateOnly(it.lastRecop),
                precision = null,
                isNonConforme = it.peiDisponibiliteTerrestre == Disponibilite.NON_CONFORME,
                isAccessibleHbe = it.penaDisponibiliteHbe == Disponibilite.DISPONIBLE,
            )
        }
    }
}
