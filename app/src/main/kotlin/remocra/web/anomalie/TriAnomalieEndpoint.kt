package remocra.web.anomalie

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.db.TriAnomalieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint
import java.util.UUID

/**
 * Endpoint permettant de gérer le tri des catégories d'anomalies ainsi que le tri des anomalies au sein
 * de leur catégorie
 */
@Produces("application/json; charset=UTF-8")
@Path("tri-nomenclature")
class TriAnomalieEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var triAnomalieRepository: TriAnomalieRepository

    @Context
    lateinit var securityContext: SecurityContext

    data class ListeTriInput(
        @param:FormParam("listeObjet")
        val listeObjet: List<UUID>,
    )

    @GET
    @Path("/anomalie/get-ordre/{idCategorie}")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getListeAnomalieOrdonnee(
        @PathParam("idCategorie") idCategorie: UUID,
    ): Response =
        Response.ok(triAnomalieRepository.getListeAnomalieOrdonnee(idCategorie)).build()

    @PUT
    @Path("/anomalie/update-ordre")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun updateAnomalieOrdre(liste: ListeTriInput): Response =
        Response.ok(triAnomalieRepository.updateAnomalieOrdre(liste.listeObjet))
            .build()

    /**
     * @See remocra.db.NomenclatureCodeLibelleRepository.getAllForAdmin
     * @See remocra.web.nomenclatures.NomenclatureCodeLibelleEndpoint.get
     */
    @GET
    @Path("/anomalie-categorie/get-ordre")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getListeAnomalieCategorieOrdonnee(): Response =
        Response.ok(triAnomalieRepository.getListeAnomalieCategorieOrdonnee()).build()

    @PUT
    @Path("/anomalie-categorie/update-ordre")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun updateAnomalieCategorieOrdre(liste: ListeTriInput): Response =
        Response.ok(triAnomalieRepository.updateAnomalieCategorieOrdre(liste.listeObjet))
            .build()
}
