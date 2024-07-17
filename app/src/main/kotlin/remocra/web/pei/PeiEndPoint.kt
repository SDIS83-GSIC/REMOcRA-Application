package remocra.web.pei

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.authn.userInfo
import remocra.data.PeiData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.usecases.pei.CreatePeiUseCase
import remocra.usecases.pei.PeiUseCase
import remocra.usecases.pei.UpdatePeiUseCase
import java.util.UUID

@Path("/pei")
@Produces(MediaType.APPLICATION_JSON)
class PeiEndPoint {

    @Inject lateinit var peiUseCase: PeiUseCase

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject lateinit var createPeiUseCase: CreatePeiUseCase

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeiWithFilter(params: Params): Response {
        val listPei = peiUseCase.getPeiWithFilter(params)
        return Response.ok(
            DataTableau(listPei, peiRepository.countAllPeiWithFilter(params)),
        )
            .build()
    }

    data class DataTableau(

        val list: List<PeiRepository.PeiForTableau>?,
        val count: Int,

    )
    data class Params(
        @QueryParam("limit")
        val limit: Int? = 10,
        @QueryParam("offset")
        val offset: Int? = 0,
        @QueryParam("filterBy")
        val filterBy: PeiRepository.Filter?,
        @QueryParam("sortBy")
        val sortBy: PeiRepository.Sort?,
    )

    @GET
    @Path("/{idPei}")
    fun getInfoPei(
        @PathParam("idPei") idPei: UUID,
    ): Response {
        return Response.ok(peiUseCase.getInfoPei(idPei)).build()
    }

    @GET
    @Path("/referentiel-for-update-pei")
    fun getReferentielUpdatePei() =
        Response.ok(peiUseCase.getInfoForUpdate()).build()

    @PUT
    @Path("/update")
    fun update(peiInput: PeiInput): Response {
        val pei: PeiData = getPeiData(peiInput)

        return Response.ok(
            updatePeiUseCase.execute(
                securityContext.userInfo,
                pei,
            ),
        ).build()
    }

    @POST
    @Path("/create")
    fun create(
        peiInput: PeiInput,
    ): Response {
        val pei = getPeiData(peiInput)
        return Response.ok(
            createPeiUseCase.execute(
                securityContext.userInfo,
                pei,
            ),
        ).build()
    }

    private fun getPeiData(peiInput: PeiInput) =
        when (peiInput.peiTypePei) {
            TypePei.PIBI -> PibiData(
                peiId = peiInput.peiId ?: UUID.randomUUID(),
                peiTypePei = peiInput.peiTypePei,
                peiNumeroInterne = peiInput.peiNumeroInterne,
                peiNumeroComplet = peiInput.peiNumeroComplet,
                peiDisponibiliteTerrestre = peiInput.peiDisponibiliteTerrestre ?: Disponibilite.INDISPONIBLE,
                peiAutoriteDeciId = peiInput.peiAutoriteDeciId,
                peiServicePublicDeciId = peiInput.peiServicePublicDeciId,
                peiMaintenanceDeciId = peiInput.peiMaintenanceDeciId,
                peiCommuneId = peiInput.peiCommuneId,
                peiVoieId = peiInput.peiVoieId,
                peiNumeroVoie = peiInput.peiNumeroVoie,
                peiSuffixeVoie = peiInput.peiSuffixeVoie,
                peiLieuDitId = peiInput.peiLieuDitId,
                peiCroisementId = peiInput.peiCroisementId,
                peiComplementAdresse = peiInput.peiComplementAdresse,
                peiEnFace = peiInput.peiEnFace,
                peiDomaineId = peiInput.peiDomaineId,
                peiNatureId = peiInput.peiNatureId,
                peiSiteId = peiInput.peiSiteId,
                peiGestionnaireId = peiInput.peiGestionnaireId,
                peiNatureDeciId = peiInput.peiNatureDeciId,
                peiZoneSpecialeId = null,
                peiAnneeFabrication = null,
                peiNiveauId = peiInput.peiNiveauId,
                peiObservation = null,
                pibiRenversable = peiInput.pibiRenversable,
                pibiDiametreId = peiInput.pibiDiametreId,
                pibiAdditive = peiInput.pibiAdditive,
                pibiSurpresse = peiInput.pibiSurpresse,
                pibiServiceEauId = peiInput.pibiServiceEauId,
                pibiTypeReseauId = peiInput.pibiTypeReseauId,
                pibiDebitRenforce = peiInput.pibiDebitRenforce,
                pibiMarqueId = peiInput.pibiMarqueId,
                pibiModeleId = peiInput.pibiModeleId,
                pibiNumeroScp = peiInput.pibiNumeroScp,
                pibiReservoirId = peiInput.pibiReservoirId,
                pibiTypeCanalisationId = peiInput.pibiTypeCanalisationId,
                pibiDiametreCanalisation = peiInput.pibiDiametreCanalisation,
                pibiDispositifInviolabilite = peiInput.pibiDispositifInviolabilite,
                peiCommuneIdInitial = peiInput.peiCommuneIdInitial,
                peiDomaineIdInitial = peiInput.peiDomaineIdInitial,
                peiNatureDeciIdInitial = peiInput.peiNatureDeciIdInitial,
                peiZoneSpecialeIdInitial = peiInput.peiZoneSpecialeIdInitial,
                peiNumeroInterneInitial = peiInput.peiNumeroInterneInitial,
            )
            TypePei.PENA -> PenaData(
                peiId = peiInput.peiId ?: UUID.randomUUID(),
                peiTypePei = peiInput.peiTypePei,
                peiNumeroInterne = peiInput.peiNumeroInterne,
                peiNumeroComplet = peiInput.peiNumeroComplet,
                peiDisponibiliteTerrestre = peiInput.peiDisponibiliteTerrestre ?: Disponibilite.INDISPONIBLE,
                peiAutoriteDeciId = peiInput.peiAutoriteDeciId,
                peiServicePublicDeciId = peiInput.peiServicePublicDeciId,
                peiMaintenanceDeciId = peiInput.peiMaintenanceDeciId,
                peiCommuneId = peiInput.peiCommuneId,
                peiVoieId = peiInput.peiVoieId,
                peiNumeroVoie = peiInput.peiNumeroVoie,
                peiSuffixeVoie = peiInput.peiSuffixeVoie,
                peiLieuDitId = peiInput.peiLieuDitId,
                peiCroisementId = peiInput.peiCroisementId,
                peiComplementAdresse = peiInput.peiComplementAdresse,
                peiEnFace = peiInput.peiEnFace,
                peiDomaineId = peiInput.peiDomaineId,
                peiNatureId = peiInput.peiNatureId,
                peiSiteId = peiInput.peiSiteId,
                peiGestionnaireId = peiInput.peiGestionnaireId,
                peiNatureDeciId = peiInput.peiNatureDeciId,
                peiZoneSpecialeId = null,
                peiAnneeFabrication = null,
                peiNiveauId = peiInput.peiNiveauId,
                peiObservation = null,
                penaCapaciteIllimitee = peiInput.penaCapaciteIllimitee,
                penaQuantiteAppoint = peiInput.penaQuantiteAppoint,
                penaDisponibiliteHbe = peiInput.penaDisponibiliteHbe ?: Disponibilite.INDISPONIBLE,
                penaMateriauId = peiInput.penaMateriauId,
                penaCapacite = peiInput.penaCapacite,
                penaCapaciteIncertaine = peiInput.penaCapaciteIncertaine,
                peiCommuneIdInitial = peiInput.peiCommuneIdInitial,
                peiDomaineIdInitial = peiInput.peiDomaineIdInitial,
                peiNatureDeciIdInitial = peiInput.peiNatureDeciIdInitial,
                peiZoneSpecialeIdInitial = peiInput.peiZoneSpecialeIdInitial,
                peiNumeroInterneInitial = peiInput.peiNumeroInterneInitial,

            )
            else -> throw IllegalArgumentException()
        }

    class PeiInput {
        @FormParam("peiId")
        val peiId: UUID? = null

        @FormParam("peiNumeroInterne")
        val peiNumeroInterne: Int? = null // Dans le cadre d'une création

        @FormParam("peiNumeroComplet")
        val peiNumeroComplet: String? = null // Dans le cadre d'une création

        @FormParam("peiTypePei")
        lateinit var peiTypePei: TypePei

        @FormParam("peiAutoriteDeciId")
        lateinit var peiAutoriteDeciId: UUID

        @FormParam("peiDisponibiliteTerrestre")
        val peiDisponibiliteTerrestre: Disponibilite? = null

        @FormParam("peiServicePublicDeciId")
        lateinit var peiServicePublicDeciId: UUID

        @FormParam("peiMaintenanceDeciId")
        var peiMaintenanceDeciId: UUID? = null

        @FormParam("peiCommuneId")
        lateinit var peiCommuneId: UUID

        @FormParam("peiVoieId")
        lateinit var peiVoieId: UUID

        @FormParam("peiNumeroVoie")
        val peiNumeroVoie: Int? = null

        @FormParam("peiSuffixeVoie")
        val peiSuffixeVoie: String? = null

        @FormParam("peiLieuDitId")
        val peiLieuDitId: UUID? = null

        @FormParam("peiCroisementId")
        val peiCroisementId: UUID? = null

        @FormParam("peiComplementAdresse")
        val peiComplementAdresse: String? = null

        @FormParam("peiEnFace")
        val peiEnFace: Boolean = false

        @FormParam("peiDomaineId")
        lateinit var peiDomaineId: UUID

        @FormParam("peiNatureId")
        lateinit var peiNatureId: UUID

        @FormParam("peiSiteId")
        var peiSiteId: UUID? = null

        @FormParam("peiGestionnaireId")
        var peiGestionnaireId: UUID? = null

        @FormParam("peiNatureDeciId")
        lateinit var peiNatureDeciId: UUID

        @FormParam("peiNiveauId")
        var peiNiveauId: UUID? = null

        // //////////////// VALEURS POUR LES PIBI
        @FormParam("pibiDiametreId")
        var pibiDiametreId: UUID? = null

        @FormParam("pibiServiceEauId")
        var pibiServiceEauId: UUID? = null

        @FormParam("pibiNumeroScp")
        var pibiNumeroScp: String? = null

        @FormParam("pibiRenversable")
        var pibiRenversable: Boolean = false

        @FormParam("pibiDispositifInviolabilite")
        var pibiDispositifInviolabilite: Boolean = false

        @FormParam("pibiModeleId")
        var pibiModeleId: UUID? = null

        @FormParam("pibiMarqueId")
        var pibiMarqueId: UUID? = null

        @FormParam("pibiReservoirId")
        var pibiReservoirId: UUID? = null

        @FormParam("pibiDebitRenforce")
        var pibiDebitRenforce: Boolean = false

        @FormParam("pibiTypeCanalisationId")
        var pibiTypeCanalisationId: UUID? = null

        @FormParam("pibiTypeReseauId")
        var pibiTypeReseauId: UUID? = null

        @FormParam("pibiDiametreCanalisation")
        var pibiDiametreCanalisation: Int? = null

        @FormParam("pibiSurpresse")
        var pibiSurpresse: Boolean = false

        @FormParam("pibiAdditive")
        var pibiAdditive: Boolean = false

        // DONNEES PENA
        @FormParam("penaMateriauId")
        var penaMateriauId: UUID? = null

        @FormParam("penaCapaciteIllimitee")
        var penaCapaciteIllimitee: Boolean = false

        @FormParam("penaCapaciteIncertaine")
        var penaCapaciteIncertaine: Boolean = false

        @FormParam("penaQuantiteAppoint")
        var penaQuantiteAppoint: Double? = null

        @FormParam("penaDisponibiliteHbe")
        val penaDisponibiliteHbe: Disponibilite? = null

        @FormParam("penaCapacite")
        var penaCapacite: Int? = null

        // On renvoie aussi les valeurs initiales pour la numérotation
        @FormParam("peiNumeroInterneInitial")
        var peiNumeroInterneInitial: Int? = null

        @FormParam("peiCommuneIdInitial")
        var peiCommuneIdInitial: UUID? = null

        @FormParam("peiZoneSpecialeIdInitial")
        var peiZoneSpecialeIdInitial: UUID? = null

        @FormParam("peiNatureDeciIdInitial")
        var peiNatureDeciIdInitial: UUID? = null

        @FormParam("peiDomaineIdInitial")
        var peiDomaineIdInitial: UUID? = null
    }
}
