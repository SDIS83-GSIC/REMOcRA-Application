package remocra.web.nomenclatures

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.db.NomenclatureAnomalieCategorieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.web.AbstractEndpoint
import java.util.UUID

/**
 * Endpoint permettant de gérer le cas particulier des catégories d'anomalies parmi les nomenclatures de type code-libellé-actif(-protected?)
 */
@Produces("application/json; charset=UTF-8")
@Path("nomenclature-anomalie-categorie") // On garde la même architecture que NomenclatureCodeLibelleEndpoint dans le cas où ceci vient un jour à être généralisée
class NomenclatureAnomalieCategorieEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var nomenclatureAnomalieCategorieRepository: NomenclatureAnomalieCategorieRepository

    @Context
    lateinit var securityContext: SecurityContext

    class ListeTriInput {
        @FormParam("listeObjet")
        lateinit var listeObjet: List<UUID>
    }

    /**
     * @See remocra.db.NomenclatureCodeLibelleRepository.getAllForAdmin
     * @See NomenclatureCodeLibelleEndpoint.get
     */
    @GET
    @Path("/get-ordre")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun getListeOrdonnee(): Response =
        Response.ok(nomenclatureAnomalieCategorieRepository.getListeOrdonnee()).build()

    @PUT
    @Path("/update-ordre")
    @RequireDroits([Droit.ADMIN_NOMENCLATURE])
    fun updateOrdre(liste: ListeTriInput): Response =
        Response.ok(nomenclatureAnomalieCategorieRepository.updateOrdre(liste.listeObjet)).build()
}
