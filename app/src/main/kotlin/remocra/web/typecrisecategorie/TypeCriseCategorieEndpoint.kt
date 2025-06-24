package remocra.web.typecrisecategorie

import jakarta.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
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
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.TypeCriseCatagorieRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeGeometry
import remocra.db.jooq.remocra.tables.pojos.TypeCriseCategorie
import remocra.usecase.crise.typecrisecategorie.CreateTypeCriseCategorieUseCase
import remocra.usecase.crise.typecrisecategorie.UpdateTypeCriseCategorieUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/type-crise-categorie")
@Produces(MediaType.APPLICATION_JSON)
class TypeCriseCategorieEndpoint : AbstractEndpoint() {

    @Inject lateinit var typeCriseCatagorieRepository: TypeCriseCatagorieRepository

    @Inject lateinit var createTypeCriseCatagorieUseCase: CreateTypeCriseCategorieUseCase

    @Inject lateinit var updateTypeCriseCatagorieUseCase: UpdateTypeCriseCategorieUseCase

    @Context lateinit var securityContext: SecurityContext

    @POST
    @Path("/")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun list(params: Params<TypeCriseCatagorieRepository.Filter, TypeCriseCatagorieRepository.Sort>): Response {
        return Response.ok(
            DataTableau(
                typeCriseCatagorieRepository.getAllForAdmin(params),
                typeCriseCatagorieRepository.countAllForAdmin(params.filterBy),
            ),
        ).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun post(element: TypeCriseCategorieInput): Response {
        return createTypeCriseCatagorieUseCase.execute(
            securityContext.userInfo,
            TypeCriseCategorie(
                typeCriseCategorieId = UUID.randomUUID(),
                typeCriseCategorieCode = element.typeCriseCategorieCode,
                typeCriseCategorieLibelle = element.typeCriseCategorieLibelle,
                typeCriseCategorieTypeGeometrie = element.typeCriseCategorieTypeGeometrie,
                typeCriseCategorieCriseCategorieId = element.typeCriseCategorieCriseCategorieId,
            ),
        ).wrap()
    }
    class TypeCriseCategorieInput {
        @FormParam("typeCriseCategorieCode")
        lateinit var typeCriseCategorieCode: String

        @FormParam("typeCriseCategorieLibelle")
        lateinit var typeCriseCategorieLibelle: String

        @FormParam("typeCriseCategorieTypeGeometrie")
        lateinit var typeCriseCategorieTypeGeometrie: TypeGeometry

        @FormParam("typeCriseCategorieTypeGeometrie")
        lateinit var typeCriseCategorieCriseCategorieId: UUID
    }

    @PUT
    @Path("/update/{typeCriseCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun put(
        @PathParam("typeCriseCategorieId")
        typeCriseCategorieId: UUID,
        element: TypeCriseCategorieInput,
    ): Response {
        return updateTypeCriseCatagorieUseCase.execute(
            securityContext.userInfo,
            TypeCriseCategorie(
                typeCriseCategorieId = typeCriseCategorieId,
                typeCriseCategorieCode = element.typeCriseCategorieCode,
                typeCriseCategorieLibelle = element.typeCriseCategorieLibelle,
                typeCriseCategorieTypeGeometrie = element.typeCriseCategorieTypeGeometrie,
                typeCriseCategorieCriseCategorieId = element.typeCriseCategorieCriseCategorieId,
            ),
        ).wrap()
    }

    @GET
    @Path("/get/{typeCriseCategorieId}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(
        @PathParam("typeCriseCategorieId")
        typeCriseCategorieId: UUID,
    ): Response {
        return Response.ok(typeCriseCatagorieRepository.getById(typeCriseCategorieId)).build()
    }
}
