package remocra.usecases.pei

import jakarta.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.data.PeiData
import remocra.db.CommuneRepository
import remocra.db.GestionnaireRepository
import remocra.db.LieuDitRepository
import remocra.db.ModelePibiRepository
import remocra.db.OrganismeRepository
import remocra.db.PeiRepository
import remocra.db.SiteRepository
import remocra.db.VoieRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.web.pei.PeiEndPoint
import java.util.UUID

/**
 * UseCase regroupant tous les services devant remonter de l'information sur les PEI. <br />
 *
 * /!\ Aucun service ne doit modifier l'état des PEI, sinon passer par les UseCases dédiés :
 * * [CreatePeiUseCase]
 * * [UpdatePeiUseCase]
 * * [DeletePeiUseCase]
 */
class PeiUseCase {

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var communeRepository: CommuneRepository

    @Inject
    lateinit var voieRepository: VoieRepository

    @Inject
    lateinit var organismeRepository: OrganismeRepository

    @Inject
    lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    lateinit var siteRepository: SiteRepository

    @Inject
    lateinit var lieuDitRepository: LieuDitRepository

    @Inject
    lateinit var modelePibiRepository: ModelePibiRepository

    fun getPeiWithFilter(param: PeiEndPoint.Params): List<PeiRepository.PeiForTableau> {
        return peiRepository.getPeiWithFilter(param)
    }

    fun getInfoPei(idPei: UUID): PeiData {
        val typePei = peiRepository.getTypePei(idPei)

        return when (typePei) {
            TypePei.PIBI -> peiRepository.getInfoPibi(idPei)
            TypePei.PENA -> peiRepository.getInfoPena(idPei)
            else -> throw IllegalArgumentException("Le type du PEI $idPei est incorrect (Valeurs autorisées : PIBI, PENA)")
        }
    }

    /**
     * Retourne les informations nécessaires pour les valeurs qui peuvent être modifié
     * dans le formaulaire d'update d'un PEI
     */
    fun getInfoForUpdate(): FichePeiListSelect {
        // TODO mettre en place les paramètres de distance pour remonter les communes et les voies et lieux dit qui sont
        //  pas trop loin du PEI en question + prendre en compte pour les organismes
        return FichePeiListSelect(
            listAutoriteDeci = organismeRepository.getAutoriteDeciForSelect(),
            listServicePublicDeci = organismeRepository.getServicePublicForSelect(),
            listMaintenanceDeci = organismeRepository.getMaintenanceDeciForSelect(),
            listCommune = communeRepository.getCommuneForSelect(),
            listSite = siteRepository.getAll(),
            listVoie = voieRepository.getVoieForSelect(),
            listGestionnaire = gestionnaireRepository.getAll(),
            listLieuDit = lieuDitRepository.getAllWithCommune(),
            listModele = modelePibiRepository.getModeleWithMarque(),
            listServiceEau = organismeRepository.getServiceEauForSelect(),
        )
    }

    // On renvoie que des listes de Id - Code - Libellé pour que ça colle avec notre SelectInput
    // Dans quelques cas, on a aussi besoin d'un id pour proposer les bonnes valeurs en fonction de celles saisies
    // (par ex : les voies sont chargées en fonction des communes, les site en fonction des gestionnaires)
    data class FichePeiListSelect(
        val listAutoriteDeci: Collection<IdCodeLibelleData>,
        val listServicePublicDeci: Collection<IdCodeLibelleData>,
        val listMaintenanceDeci: Collection<IdCodeLibelleData>,
        val listGestionnaire: Collection<IdCodeLibelleData>,
        val listSite: Collection<SiteRepository.SiteWithGestionnaire>,
        val listCommune: Collection<IdCodeLibelleData>,
        val listLieuDit: Collection<LieuDitRepository.LieuDitWithCommune>,
        val listVoie: Collection<VoieRepository.VoieWithCommune>,
        val listModele: Collection<ModelePibiRepository.ModeleWithMarque>,
        val listServiceEau: Collection<IdCodeLibelleData>,
    )
}
