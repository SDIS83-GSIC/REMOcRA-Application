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
import org.locationtech.jts.geom.Geometry
import remocra.auth.Public
import remocra.auth.userInfo
import remocra.data.enums.TypeElementCarte
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import remocra.usecase.carte.GetPointCarteUseCase
import remocra.usecase.dfci.GetDfciPisteUseCase
import remocra.usecase.dfci.GetIdCodeLibelleDfciPistesUseCase
import remocra.usecase.dfci.UpdatePisteUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID
import kotlin.String

/**
 * Classe Endpoint pour les différentes requêtes sur les pistes du module DFCI
 */
@Path("/dfci-pistes")
@Produces(MediaType.APPLICATION_JSON)
class DfciPistesEndpoint : AbstractEndpoint() {

    @Inject
    lateinit var getIdCodeLibelleDfciPistesUseCase: GetIdCodeLibelleDfciPistesUseCase

    @Inject
    private lateinit var getDfciPisteUseCase: GetDfciPisteUseCase

    @Inject
    private lateinit var updatePisteUseCase: UpdatePisteUseCase

    @Inject
    private lateinit var getPointCarteUseCase: GetPointCarteUseCase

    @Context
    private lateinit var securityContext: SecurityContext

    @GET
    @Path("/piste-id-code-libelle")
    @Public("En attente du tableau de droit")
    fun getPisteIdCodeLibelle(
        @QueryParam("geometrie") geometrie: Geometry,
    ): Response =
        Response.ok(getIdCodeLibelleDfciPistesUseCase.execute(geometrie)).build()

    /**
     * Endpoint pour récupérer les informations d'une piste suivant l'id donné
     */
    @GET
    @Path("/{pisteId}")
    @Public("En attente du tableau de droit")
    fun getDfciPisteById(@PathParam("pisteId") pisteId: UUID): Response =
        Response.ok(
            getDfciPisteUseCase.execute(pisteId),
        ).build()

    /**
     * Endpoint permettant d'avoir le layer de carte contenant les pistes
     */
    @GET
    @Path("/layer")
    @Public("En attente du tableau de droit")
    fun layer(@QueryParam("bbox") bbox: String, @QueryParam("srid") srid: String): Response =
        Response.ok(
            getPointCarteUseCase.execute(
                bbox,
                srid,
                null,
                TypeElementCarte.DFCI_PISTE,
                securityContext.userInfo,
            ),
        ).build()

    @PUT
    @Path("/update")
    @Public("En attente du tableau de droit")
    fun update(dfciPiste: DfciPiste): Response =
        Response.ok(
            updatePisteUseCase.execute(
                securityContext.userInfo,
                DfciPiste(
                    dfciPisteId = dfciPiste.dfciPisteId,
                    dfciPisteAdresse = dfciPiste.dfciPisteAdresse,
                    dfciPisteAnneeProgramme = dfciPiste.dfciPisteAnneeProgramme,
                    dfciPisteAnneeTravaux = dfciPiste.dfciPisteAnneeTravaux,
                    dfciPisteCirculation = dfciPiste.dfciPisteCirculation,
                    dfciPisteDateGps = dfciPiste.dfciPisteDateGps,
                    dfciPisteLibelle = dfciPiste.dfciPisteLibelle,
                    dfciPisteNumero = dfciPiste.dfciPisteNumero,
                    dfciPisteOuverture = dfciPiste.dfciPisteOuverture,
                    dfciPisteEstDfci = dfciPiste.dfciPisteEstDfci,
                    dfciPisteRetournement = dfciPiste.dfciPisteRetournement,
                    dfciPisteNumTroncon = dfciPiste.dfciPisteNumTroncon,
                    dfciPisteNumObjectif = dfciPiste.dfciPisteNumObjectif,
                    dfciPisteLibelleObjectif = dfciPiste.dfciPisteLibelleObjectif,
                    dfciPisteGeometrie = dfciPiste.dfciPisteGeometrie,
                    dfciPisteImpraticabilite = dfciPiste.dfciPisteImpraticabilite,
                    dfciPisteTravaux = dfciPiste.dfciPisteTravaux,
                    dfciPisteVoie = dfciPiste.dfciPisteVoie,
                    dfciPisteImpasse = dfciPiste.dfciPisteImpasse,
                    dfciPisteFoncier = dfciPiste.dfciPisteFoncier,
                    dfciPisteCroisement = dfciPiste.dfciPisteCroisement,
                    dfciPistePraticabilite = dfciPiste.dfciPistePraticabilite,
                    dfciPisteProgramme = dfciPiste.dfciPisteProgramme,
                    dfciPisteRemarque = dfciPiste.dfciPisteRemarque,
                    dfciPisteDfciCategoriePisteId = dfciPiste.dfciPisteDfciCategoriePisteId,
                    dfciPisteDfciMassifId = dfciPiste.dfciPisteDfciMassifId,
                    dfciPisteDfciPrestataireId = dfciPiste.dfciPisteDfciPrestataireId,
                    dfciPisteDfciOuvrageId = dfciPiste.dfciPisteDfciOuvrageId,
                    dfciPisteCode = dfciPiste.dfciPisteCode,
                    dfciPisteVersion = dfciPiste.dfciPisteVersion + 1, // On incrémente la version de la ligne à chaque update
                ),
            ),
        ).build()
}
