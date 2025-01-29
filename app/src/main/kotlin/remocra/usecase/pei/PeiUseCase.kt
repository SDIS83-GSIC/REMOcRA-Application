package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.DataTableau
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.data.Params
import remocra.data.PeiData
import remocra.data.enums.TypeAutoriteDeci
import remocra.db.CommuneRepository
import remocra.db.DiametreRepository
import remocra.db.GestionnaireRepository
import remocra.db.LieuDitRepository
import remocra.db.ModelePibiRepository
import remocra.db.OrganismeRepository
import remocra.db.PeiRepository
import remocra.db.PenaRepository
import remocra.db.PibiRepository
import remocra.db.SiteRepository
import remocra.db.VoieRepository
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecase.AbstractUseCase
import remocra.usecase.messagelongueindispo.GetMessagePeiLongueIndispoUseCase
import java.util.UUID

/**
 * UseCase regroupant tous les services devant remonter de l'information sur les PEI. <br />
 *
 * /!\ Aucun service ne doit modifier l'état des PEI, sinon passer par les UseCases dédiés :
 * * [CreatePeiUseCase]
 * * [UpdatePeiUseCase]
 * * [DeletePeiUseCase]
 */
class PeiUseCase : AbstractUseCase() {

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var pibiRepository: PibiRepository

    @Inject
    lateinit var penaRepository: PenaRepository

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

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var diametreRepository: DiametreRepository

    @Inject
    lateinit var getMessagePeiLongueIndispoUseCase: GetMessagePeiLongueIndispoUseCase

    @Inject
    lateinit var appSettings: AppSettings

    fun getPeiWithFilter(params: Params<PeiRepository.Filter, PeiRepository.Sort>, userInfo: UserInfo): List<PeiRepository.PeiForTableau> {
        val listePei = peiRepository.getPeiWithFilter(params, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)

        /*
         * Le libelle de la tournée est un multiset qui concatène toutes les tournées
         * Impossible en jooq de filtrer sur un multiset
         */
        params.filterBy?.tourneeLibelle?.let {
                tourneeSearch ->
            return listePei.filter { it.tourneeLibelle?.contains(tourneeSearch) == true }
        }

        return peiRepository.getPeiWithFilter(params, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)
    }

    fun getPeiWithFilterByIndisponibiliteTemporaire(
        param: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
        idIndisponibiliteTemporaire: UUID,
        userInfo: UserInfo,
    ) = peiRepository.getPeiWithFilterByIndisponibiliteTemporaire(param, idIndisponibiliteTemporaire, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)

    fun getPeiWithFilterByTournee(
        param: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
        idTournee: UUID,
        userInfo: UserInfo,
    ): List<PeiRepository.PeiForTableau> {
        param.filterBy?.idTournee = idTournee
        param.sortBy?.ordreTournee = 1
        return peiRepository.getPeiWithFilterByTournee(param, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin)
    }

    fun getInfoPei(idPei: UUID): PeiData {
        val typePei = peiRepository.getTypePei(idPei)

        return when (typePei) {
            TypePei.PIBI -> pibiRepository.getInfoPibi(idPei)
            TypePei.PENA -> penaRepository.getInfoPena(idPei)
            else -> throw IllegalArgumentException("Le type du PEI $idPei est incorrect (Valeurs autorisées : PIBI, PENA)")
        }
    }

    /**
     * Retourne les informations nécessaires pour les valeurs qui peuvent être modifié
     * dans le formaulaire d'update d'un PEI
     */
    fun getInfoForUpdateOrCreate(coordonneeX: String?, coordonneeY: String?, peiId: UUID?): FichePeiListSelect {
        val srid = appSettings.srid

        val toleranceCommune = parametresProvider.getParametreInt(GlobalConstants.PEI_TOLERANCE_COMMUNE_METRES)
            ?: throw IllegalArgumentException("Le paramètre PEI_TOLERANCE_COMMUNE_METRES est nul, veuillez renseigner une valeur")
        val toleranceVoie = parametresProvider.getParametreInt(GlobalConstants.TOLERANCE_VOIES_METRES)
            ?: throw IllegalArgumentException("Le paramètre TOLERANCE_VOIES_METRES est nul, veuillez renseigner une valeur")

        var listCommune: Collection<IdCodeLibelleData> = listOf()

        var listVoiePei: Collection<VoieRepository.VoieWithCommune> = listOf()
        var listAutoriteDeci: Collection<OrganismeRepository.OrganismePei> = listOf()
        var listServicePublicDeci: Collection<IdCodeLibelleData> = listOf()
        var listMaintenanceDeci: Collection<IdCodeLibelleData> = listOf()
        var listLieuDit: Collection<LieuDitRepository.LieuDitWithCommune> = listOf()
        var listPeiJumelage: Collection<IdCodeLibelleData> = listOf()

        if (!coordonneeX.isNullOrEmpty() && !coordonneeY.isNullOrEmpty()) {
            listCommune = communeRepository.getCommunesPei(coordonneeX, coordonneeY, srid, toleranceCommune)
            val listIdCommune = listCommune.map { it.id }
            listVoiePei = voieRepository.getVoies(coordonneeX, coordonneeY, srid, toleranceVoie, listIdCommune)
            listAutoriteDeci = organismeRepository
                .getAutoriteDeciPei(coordonneeX, coordonneeY, srid, toleranceCommune).onEach {
                    when (it.codeTypeOrganisme.uppercase()) {
                        TypeAutoriteDeci.COMMUNE.name.uppercase() -> it.libelle = "Maire (${it.libelle})"
                        TypeAutoriteDeci.PREFECTURE.name.uppercase() -> it.libelle = "Préfet (${it.libelle})"
                        TypeAutoriteDeci.EPCI.name.uppercase() -> it.libelle = "Président (${it.libelle})"
                    }
                }

            listLieuDit = lieuDitRepository.getLieuDitWithCommunePei(listIdCommune)
            listPeiJumelage = pibiRepository.getBiCanJumele(coordonneeX, coordonneeY, peiId, srid)
            listMaintenanceDeci =
                organismeRepository.getMaintenanceDeciPei(coordonneeX, coordonneeY, srid, toleranceCommune)
                    .map { IdCodeLibelleData(it.id, it.code, it.libelle) }

            listServicePublicDeci =
                organismeRepository.getServicePublicDeciPei(coordonneeX, coordonneeY, srid, toleranceCommune)
                    .map { IdCodeLibelleData(it.id, it.code, it.libelle) }
        }

        return FichePeiListSelect(
            listAutoriteDeci = listAutoriteDeci.map { IdCodeLibelleData(it.id, it.code, it.libelle) },
            listServicePublicDeci = listServicePublicDeci,
            listMaintenanceDeci = listMaintenanceDeci,
            listCommune = listCommune,
            listSite = siteRepository.getAll(),
            listVoie = listVoiePei,
            listGestionnaire = gestionnaireRepository.getAll(),
            listLieuDit = listLieuDit,
            listModele = modelePibiRepository.getModeleWithMarque(),
            listServiceEau = organismeRepository.getServiceEauForSelect(),
            listPeiJumelage = listPeiJumelage,
            listDiametreWithNature = diametreRepository.getDiametreWithIdNature(),
        )
    }

    fun getPeiWithFilterByMessageAlerteForDataTableau(
        params: Params<
            PeiRepository.Filter,
            PeiRepository.Sort,
            >,
        userInfo: UserInfo,
    ): DataTableau<PeiRepository.PeiForTableau> {
        val listePeiId = getMessagePeiLongueIndispoUseCase.getListePeiAlerte(userInfo) ?: setOf()
        return DataTableau(
            list = peiRepository.getPeiWithFilterByMessageAlerte(
                params,
                listePeiId,
                userInfo.zoneCompetence?.zoneIntegrationId,
                userInfo.isSuperAdmin,
            ),
            count = peiRepository.countAllPeiWithFilterByMessageAlerte(
                params.filterBy,
                listePeiId,
                userInfo.zoneCompetence?.zoneIntegrationId,
                userInfo.isSuperAdmin,
            ),
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
        val listSite: Collection<SiteRepository.SiteWithGestionnaireId>,
        val listCommune: Collection<IdCodeLibelleData>,
        val listLieuDit: Collection<LieuDitRepository.LieuDitWithCommune>,
        val listVoie: Collection<VoieRepository.VoieWithCommune>,
        val listModele: Collection<ModelePibiRepository.ModeleWithMarque>,
        val listServiceEau: Collection<IdCodeLibelleData>,
        val listPeiJumelage: Collection<IdCodeLibelleData>,
        val listDiametreWithNature: Collection<DiametreRepository.DiametreWithNature>,
    )
}
