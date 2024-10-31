package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.DataTableau
import remocra.data.LienProfilFonctionnaliteUpdateData
import remocra.data.Params
import remocra.db.LienProfilFonctionnaliteRepository
import remocra.db.ProfilDroitRepository
import remocra.db.ProfilOrganismeRepository
import remocra.db.ProfilUtilisateurRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LProfilUtilisateurOrganismeDroit
import remocra.usecase.admin.lienprofilfonctionnalite.CreateLienProfilFonctionnaliteUseCase
import remocra.usecase.admin.lienprofilfonctionnalite.DeleteLienProfilFonctionnaliteUseCase
import remocra.usecase.admin.lienprofilfonctionnalite.UpdateLienProfilFonctionnaliteUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Produces("application/json; charset=UTF-8")
@Path("/lien-profil-fonctionnalite")
class LienProfilFonctionnaliteEndpoint : AbstractEndpoint() {
    @Context lateinit var securityContext: SecurityContext

    @Inject lateinit var lienProfilFonctionnaliteRepository: LienProfilFonctionnaliteRepository

    @Inject lateinit var profilOrganismeRepository: ProfilOrganismeRepository

    @Inject lateinit var profilUtilisateurRepository: ProfilUtilisateurRepository

    @Inject lateinit var profilDroitRepository: ProfilDroitRepository

    @Inject lateinit var createLienProfilFonctionnaliteUseCase: CreateLienProfilFonctionnaliteUseCase

    @Inject lateinit var updateLienProfilFonctionnaliteUseCase: UpdateLienProfilFonctionnaliteUseCase

    @Inject lateinit var deleteLienProfilFonctionnaliteUseCase: DeleteLienProfilFonctionnaliteUseCase

    @Path("/")
    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<LienProfilFonctionnaliteRepository.Filter, LienProfilFonctionnaliteRepository.Sort>): Response =
        Response.ok(
            DataTableau(
                lienProfilFonctionnaliteRepository.getAll(params),
                lienProfilFonctionnaliteRepository.getCountAll(params),
            ),
        ).build()

    @Path("/{profilOrganismeId}/{profilUtilisateurId}")
    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(
        @PathParam("profilOrganismeId") profilOrganismeId: UUID,
        @PathParam("profilUtilisateurId") profilUtilisateurId: UUID,
    ): Response =
        Response.ok(lienProfilFonctionnaliteRepository.get(profilOrganismeId, profilUtilisateurId)).build()

    @Path("/referentiel")
    @GET
    @RequireDroits([Droit.ADMIN_DROITS])
    fun referentiel(): Response {
        return Response.ok(
            object {
                val profilOrganismeList = profilOrganismeRepository.getActive()
                val profilUtilisateurList = profilUtilisateurRepository.getAllActive()
                val profilDroitList = profilDroitRepository.getAllActive()
            },
        ).build()
    }

    @Path("/create")
    @POST
    @RequireDroits([Droit.ADMIN_DROITS])
    fun post(element: LProfilUtilisateurOrganismeDroit): Response =
        createLienProfilFonctionnaliteUseCase.execute(securityContext.userInfo, element).wrap()

    @Path("/update/{profilOrganismeId}/{profilUtilisateurId}")
    @PUT
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(
        @PathParam("profilOrganismeId") profilOrganismeId: UUID,
        @PathParam("profilUtilisateurId") profilUtilisateurId: UUID,
        element: LProfilUtilisateurOrganismeDroit,
    ): Response =
        updateLienProfilFonctionnaliteUseCase.execute(
            securityContext.userInfo,
            LienProfilFonctionnaliteUpdateData(
                profilOrganismeId = profilOrganismeId,
                profilUtilisateurId = profilUtilisateurId,
                newValue = element,
            ),
        ).wrap()

    @Path("/delete")
    @DELETE
    @RequireDroits([Droit.ADMIN_DROITS])
    fun delete(element: LProfilUtilisateurOrganismeDroit): Response =
        deleteLienProfilFonctionnaliteUseCase.execute(securityContext.userInfo, element).wrap()
}
