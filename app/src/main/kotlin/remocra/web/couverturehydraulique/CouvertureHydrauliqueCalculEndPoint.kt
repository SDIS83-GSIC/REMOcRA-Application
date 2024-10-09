package remocra.web.couverturehydraulique

import com.google.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.data.couverturehydraulique.CalculData
import remocra.db.CouvertureHydrauliqueCalculRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.couverturehydraulique.CalculCouvertureUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/couverture-hydraulique/calcul")
@Produces(MediaType.APPLICATION_JSON)
class CouvertureHydrauliqueCalculEndPoint : AbstractEndpoint() {
    @Inject
    lateinit var calculCouvertureUseCase: CalculCouvertureUseCase

    @Inject
    lateinit var couvertureHydrauliqueCalculRepository: CouvertureHydrauliqueCalculRepository

    @Context
    lateinit var securityContext: SecurityContext

    @PUT
    @Path("/{etudeId}")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun calcul(
        @PathParam("etudeId")
        etudeId: UUID,
        peiSelectionnes: PeiSelectionnes,
    ): Response {
        return calculCouvertureUseCase.execute(
            securityContext.userInfo,
            CalculData(
                listPeiId = peiSelectionnes.listePeiId,
                listPeiProjetId = peiSelectionnes.listePeiProjetId,
                useReseauImporte = peiSelectionnes.useReseauImporte,
                useReseauImporteWithReseauCourant = peiSelectionnes.useReseauImporteWithReseauCourant,
                etudeId = etudeId,
            ),
        ).wrap()
    }

    data class PeiSelectionnes(
        val listePeiId: Set<UUID>,
        val listePeiProjetId: Set<UUID>,
        val useReseauImporte: Boolean,
        val useReseauImporteWithReseauCourant: Boolean,
    )

    @DELETE
    @Path("/clear/{etudeId}")
    @RequireDroits([Droit.ETUDE_U])
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteCouverture(
        @PathParam("etudeId")
        etudeId: UUID,
    ): Response {
        return Response.ok(couvertureHydrauliqueCalculRepository.deleteCouverture(etudeId)).build()
    }
}
