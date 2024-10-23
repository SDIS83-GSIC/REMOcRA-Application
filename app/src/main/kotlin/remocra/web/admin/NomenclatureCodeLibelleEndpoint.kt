package remocra.web.admin

import jakarta.inject.Inject
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.FormParam
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
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.Params
import remocra.data.enums.TypeNomenclatureCodeLibelle
import remocra.db.NomenclatureCodeLibelleRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.usecase.nomenclaturecodelibelle.CreateNomenclatureCodeLibelleUseCase
import remocra.usecase.nomenclaturecodelibelle.DeleteNomenclatureCodeLibelleUseCase
import remocra.usecase.nomenclaturecodelibelle.UpdateNomenclatureCodeLibelleUseCase
import remocra.web.AbstractEndpoint
import java.util.Locale
import java.util.UUID

/**
 * Endpoint permettant de gérer toutes les nomenclatures de type code-libellé-actif(-protected?) de manière générique
 */
@Produces("application/json; charset=UTF-8")
@Path("/{typeObjet}")
class NomenclatureCodeLibelleEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var nomenclatureCodeLibelleRepository: NomenclatureCodeLibelleRepository

    @Inject
    lateinit var createNomenclatureCodeLibelleUseCase: CreateNomenclatureCodeLibelleUseCase

    @Inject
    lateinit var updateNomenclatureCodeLibelleUseCase: UpdateNomenclatureCodeLibelleUseCase

    @Inject
    lateinit var deleteNomenclatureCodeLibelleUseCase: DeleteNomenclatureCodeLibelleUseCase

    @Context
    lateinit var securityContext: SecurityContext

    class NomenclatureCodeLibelleInput {
        @FormParam("code")
        lateinit var code: String

        @FormParam("libelle")
        lateinit var libelle: String

        @FormParam("actif")
        val actif: Boolean = true

        @FormParam("protected")
        val protected: Boolean = false

        @FormParam("idFk")
        val idFk: UUID? = null
    }

    @POST
    @Path("/get")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun get(@PathParam("typeObjet") typeNomenclatureCodeLibelleString: String, params: Params<NomenclatureCodeLibelleRepository.Filter, NomenclatureCodeLibelleRepository.Sort>): Response {
        val type = getTypeNomenclatureFromString(typeNomenclatureCodeLibelleString)
        return Response.ok(DataTableau(nomenclatureCodeLibelleRepository.getAll(type, params), nomenclatureCodeLibelleRepository.getCount(type, params))).build()
    }

    @GET
    @Path("/get/{id}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun id(@PathParam("typeObjet") typeNomenclatureCodeLibelleString: String, @PathParam("id") id: UUID): Response {
        return Response.ok(nomenclatureCodeLibelleRepository.getById(getTypeNomenclatureFromString(typeNomenclatureCodeLibelleString), id)).build()
    }

    @POST
    @Path("/create")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun create(@PathParam("typeObjet") typeNomenclatureCodeLibelleString: String, nomenclatureCodeLibelleInput: NomenclatureCodeLibelleInput): Response {
        createNomenclatureCodeLibelleUseCase.setType(getTypeNomenclatureFromString(typeNomenclatureCodeLibelleString))
        return createNomenclatureCodeLibelleUseCase.execute(
            securityContext.userInfo,
            NomenclatureCodeLibelleData(
                id = UUID.randomUUID(),
                code = nomenclatureCodeLibelleInput.code,
                libelle = nomenclatureCodeLibelleInput.libelle,
                actif = nomenclatureCodeLibelleInput.actif,
                protected = nomenclatureCodeLibelleInput.protected,
                idFk = nomenclatureCodeLibelleInput.idFk,
                libelleFk = null,
            ),
        ).wrap()
    }

    @PUT
    @Path("/update/{id}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun update(@PathParam("typeObjet") typeNomenclatureCodeLibelleString: String, @PathParam("id") id: UUID, nomenclatureCodeLibelleInput: NomenclatureCodeLibelleInput): Response {
        updateNomenclatureCodeLibelleUseCase.setType(getTypeNomenclatureFromString(typeNomenclatureCodeLibelleString))
        return updateNomenclatureCodeLibelleUseCase.execute(
            securityContext.userInfo,
            NomenclatureCodeLibelleData(
                id = id,
                code = nomenclatureCodeLibelleInput.code,
                libelle = nomenclatureCodeLibelleInput.libelle,
                actif = nomenclatureCodeLibelleInput.actif,
                protected = nomenclatureCodeLibelleInput.protected,
                idFk = nomenclatureCodeLibelleInput.idFk,
                libelleFk = null,
            ),
        ).wrap()
    }

    @DELETE
    @Path("/delete/{id}")
    @RequireDroits([Droit.ADMIN_DROITS])
    fun delete(@PathParam("typeObjet") typeNomenclatureCodeLibelleString: String, @PathParam("id") id: UUID): Response {
        val type = getTypeNomenclatureFromString(typeNomenclatureCodeLibelleString)
        deleteNomenclatureCodeLibelleUseCase.setType(type)
        return deleteNomenclatureCodeLibelleUseCase.execute(securityContext.userInfo, nomenclatureCodeLibelleRepository.getById(type, id)!!).wrap()
    }

    private fun getTypeNomenclatureFromString(typeNomenclatureString: String): TypeNomenclatureCodeLibelle {
        val type = TypeNomenclatureCodeLibelle.valueOf(typeNomenclatureString.uppercase(Locale.getDefault()))
        // On laisse planter si le type n'est pas acceptable
        return type
    }
}
