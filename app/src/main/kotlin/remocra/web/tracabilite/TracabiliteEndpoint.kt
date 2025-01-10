package remocra.web.tracabilite

import com.google.inject.Inject
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.data.enums.TypeSourceModification
import remocra.data.tracabilite.Search
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.tracabilite.TracabiliteUseCase
import remocra.web.AbstractEndpoint
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

@Path("/tracabilite")
@Produces(MediaType.APPLICATION_JSON)
class TracabiliteEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var tracabiliteUseCase: TracabiliteUseCase

    @GET
    @Path("refs")
    @Public("référentiels de données de recherche")
    fun getFormReferences(): Response {
        val typeOperations = enumValues<TypeOperation>().map { it.name }
        val typeObjets = enumValues<TypeObjet>().map { it.name }
        val typeUtilisateurs = enumValues<TypeSourceModification>().map { it.name }
        return Response.ok(
            mapOf(
                "typeOperations" to typeOperations,
                "typeObjets" to typeObjets,
                "typeUtilisateurs" to typeUtilisateurs,
            ),
        ).build()
    }

    @GET
    @Path("search")
    // TODO: trouver le bon droit !
    @RequireDroits([Droit.ADMIN_PARAM_APPLI])
    fun search(@BeanParam searchParams: SearchParams): Response {
        val debut = searchParams.debut?.let {
            try {
                LocalDateTime.parse(it)
            } catch (e: DateTimeParseException) {
                null
            }
        }
        val fin = searchParams.fin?.let {
            try {
                LocalDateTime.parse(it)
            } catch (e: DateTimeParseException) {
                null
            }
        }

        val s =
            Search(
                searchParams.typeObjet,
                searchParams.typeOperation,
                searchParams.typeUtilisateur,
                debut,
                fin,
                searchParams.utilisateur,
                searchParams.objetId,
            )

        return Response.ok(tracabiliteUseCase.search(s)).build()
    }

    class SearchParams {
        @QueryParam("typeObjet")
        val typeObjet: TypeObjet? = null

        @QueryParam("typeOperation")
        val typeOperation: TypeOperation? = null

        @QueryParam("typeUtilisateur")
        var typeUtilisateur: TypeSourceModification? = null

        @QueryParam("debut")
        var debut: String? = null

        @QueryParam("fin")
        var fin: String? = null

        @QueryParam("utilisateur")
        var utilisateur: String? = null

        @QueryParam("objetId")
        var objetId: UUID? = null
    }
}
