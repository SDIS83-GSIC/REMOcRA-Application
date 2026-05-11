package remocra.web.dfci

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.dfci.GetDfciDebUseCase
import remocra.usecase.dfci.UpdateDfciDebUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID
import kotlin.String

/**
 * Classe Endpoint pour les différentes requêtes sur les débroussaillements du module DFCI
 */
@Path("/dfci-deb")
@Produces(MediaType.APPLICATION_JSON)
class DfciDebEndpoint : AbstractEndpoint() {

    @Inject
    private lateinit var getDfciDebUseCase: GetDfciDebUseCase

    @Inject
    private lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Inject
    private lateinit var updateDfciDebUseCase: UpdateDfciDebUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    @GET
    @Path("/{dfciDebId}")
    @Public("En attente du tableau de droit")
    fun getDebById(@PathParam("dfciDebId") debId: UUID): Response =
        Response.ok(
            getDfciDebUseCase.execute(debId),
        ).build()

    @GET
    @Path("/layer")
    @Public("En attente du tableau de droit")
    fun layer(@QueryParam("bbox") bbox: String, @QueryParam("srid") srid: String): Response =
        Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.DFCI_DEB,
                securityContext.userInfo,
            ),
        ).build()

    @PUT
    @Path("/update")
    @Public("En attente du tableau de droit")
    fun updateDeb(dfciDeb: DfciDeb): Response =
        Response.ok(
            updateDfciDebUseCase.execute(
                securityContext.userInfo,
                DfciDeb(
                    dfciDebId = dfciDeb.dfciDebId,
                    dfciDebLibelle = dfciDeb.dfciDebLibelle,
                    dfciDebAnneeProgramme = dfciDeb.dfciDebAnneeProgramme,
                    dfciDebAnneeTravaux = dfciDeb.dfciDebAnneeTravaux,
                    dfciDebMoisTravaux = dfciDeb.dfciDebMoisTravaux,
                    dfciDebAnneeEdition = dfciDeb.dfciDebAnneeEdition,
                    dfciDebLargeur = dfciDeb.dfciDebLargeur,
                    dfciDebSurface = dfciDeb.dfciDebSurface,
                    dfciDebGeometrie = dfciDeb.dfciDebGeometrie,
                    dfciDebRemarque = dfciDeb.dfciDebRemarque,
                    dfciDebType = dfciDeb.dfciDebType,
                    dfciDebProgramme = dfciDeb.dfciDebProgramme,
                    dfciDebTravaux = dfciDeb.dfciDebTravaux,
                    dfciDebDfciMassifId = dfciDeb.dfciDebDfciMassifId,
                    dfciDebDfciOuvrageId = dfciDeb.dfciDebDfciOuvrageId,
                    dfciDebDfciPrestataireId = dfciDeb.dfciDebDfciPrestataireId,
                    dfciDebCode = dfciDeb.dfciDebCode,
                    dfciDebVersion = dfciDeb.dfciDebVersion + 1, // On incrémente la version de la ligne à chaque update
                ),
            ),
        ).build()
}
