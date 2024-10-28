package remocra.web.contact

import com.google.inject.Inject
import jakarta.ws.rs.DELETE
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
import remocra.data.ContactData
import remocra.data.DataTableau
import remocra.data.Params
import remocra.db.ContactRepository
import remocra.db.FonctionContactRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.usecase.gestionnaire.CreateContactUseCase
import remocra.usecase.gestionnaire.DeleteContactUseCase
import remocra.usecase.gestionnaire.UpdateContactUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/contact")
@Produces(MediaType.APPLICATION_JSON)
class ContactEndPoint : AbstractEndpoint() {

    @Inject
    lateinit var createContactUseCase: CreateContactUseCase

    @Inject
    lateinit var updateContactUseCase: UpdateContactUseCase

    @Inject
    lateinit var deleteContactUseCase: DeleteContactUseCase

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var fonctionContactRepository: FonctionContactRepository

    @Context
    lateinit var securityContext: SecurityContext

    @POST
    @Path("{appartenanceId}/create")
    @RequireDroits([Droit.GEST_SITE_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        @PathParam("appartenanceId")
        appartenanceId: UUID,
        contactInput: ContactInput,
    ): Response {
        val isGestionnaire = contactRepository.checkIsGestionnaire(appartenanceId)

        return createContactUseCase.execute(
            securityContext.userInfo,
            ContactData(
                contactId = UUID.randomUUID(),
                appartenanceId = appartenanceId,
                siteId = contactInput.siteId,
                contactActif = contactInput.contactActif,
                contactCivilite = contactInput.contactCivilite,
                contactFonctionContactId = contactInput.contactFonctionContactId,
                contactNom = contactInput.contactNom,
                contactPrenom = contactInput.contactPrenom,
                contactNumeroVoie = contactInput.contactNumeroVoie,
                contactSuffixe = contactInput.contactSuffixeVoie,
                contactLieuDitText = contactInput.contactLieuDitText,
                contactLieuDitId = contactInput.contactLieuDitId,
                contactVoieText = contactInput.contactVoieText,
                contactVoieId = contactInput.contactVoieId,
                contactCommuneId = contactInput.contactCommuneId,
                contactCommuneText = contactInput.contactCommuneText,
                contactCodePostal = contactInput.contactCodePostal,
                contactPays = contactInput.contactPays,
                contactTelephone = contactInput.contactTelephone,
                contactEmail = contactInput.contactEmail,
                listRoleId = contactInput.listRoleId,
                isGestionnaire = isGestionnaire,
            ),
        ).wrap()
    }

    @PUT
    @Path("{appartenanceId}/update/{contactId}")
    @RequireDroits([Droit.GEST_SITE_A])
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("appartenanceId")
        appartenanceId: UUID,
        @PathParam("contactId")
        contactId: UUID,
        contactInput: ContactInput,
    ): Response {
        val isGestionnaire = contactRepository.checkIsGestionnaire(appartenanceId)
        return updateContactUseCase.execute(
            securityContext.userInfo,
            ContactData(
                contactId = contactId,
                appartenanceId = appartenanceId,
                siteId = contactInput.siteId,
                contactActif = contactInput.contactActif,
                contactCivilite = contactInput.contactCivilite,
                contactFonctionContactId = contactInput.contactFonctionContactId,
                contactNom = contactInput.contactNom,
                contactPrenom = contactInput.contactPrenom,
                contactNumeroVoie = contactInput.contactNumeroVoie,
                contactSuffixe = contactInput.contactSuffixeVoie,
                contactLieuDitText = contactInput.contactLieuDitText,
                contactLieuDitId = contactInput.contactLieuDitId,
                contactVoieText = contactInput.contactVoieText,
                contactVoieId = contactInput.contactVoieId,
                contactCommuneId = contactInput.contactCommuneId,
                contactCommuneText = contactInput.contactCommuneText,
                contactCodePostal = contactInput.contactCodePostal,
                contactPays = contactInput.contactPays,
                contactTelephone = contactInput.contactTelephone,
                contactEmail = contactInput.contactEmail,
                listRoleId = contactInput.listRoleId,
                isGestionnaire = isGestionnaire,
            ),
        ).wrap()
    }

    class ContactInput {
        @FormParam("contactActif")
        var contactActif: Boolean = true

        @FormParam("contactCivilite")
        var contactCivilite: TypeCivilite? = null

        @FormParam("contactFonction")
        var contactFonctionContactId: UUID? = null

        @FormParam("contactNom")
        var contactNom: String? = null

        @FormParam("contactPrenom")
        var contactPrenom: String? = null

        @FormParam("contactNumeroVoie")
        var contactNumeroVoie: String? = null

        @FormParam("contactSuffixeVoie")
        var contactSuffixeVoie: String? = null

        @FormParam("contactLieuDitText")
        var contactLieuDitText: String? = null

        @FormParam("contactLieuDitId")
        var contactLieuDitId: UUID? = null

        @FormParam("contactVoieText")
        var contactVoieText: String? = null

        @FormParam("contactVoieId")
        var contactVoieId: UUID? = null

        @FormParam("contactCommuneId")
        var contactCommuneId: UUID? = null

        @FormParam("contactCommuneText")
        var contactCommuneText: String? = null

        @FormParam("contactCodePostal")
        var contactCodePostal: String? = null

        @FormParam("contactPays")
        var contactPays: String? = null

        @FormParam("contactTelephone")
        var contactTelephone: String? = null

        @FormParam("contactEmail")
        var contactEmail: String? = null

        @FormParam("listRoleId")
        lateinit var listRoleId: List<UUID>

        @FormParam("siteId")
        var siteId: UUID? = null
    }

    @POST
    @Path("/{appartenanceId}")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getAllForAdmin(
        @PathParam("appartenanceId")
        appartenanceId: UUID,

        params: Params<ContactRepository.Filter, ContactRepository.Sort>,
    ): Response {
        val isGestionnaire = contactRepository.checkIsGestionnaire(appartenanceId)
        return Response.ok(
            DataTableau(
                list = contactRepository.getAllForAdmin(params, appartenanceId, isGestionnaire),
                count = contactRepository.countAllForAdmin(params.filterBy, appartenanceId, isGestionnaire),
            ),
        ).build()
    }

    @GET
    @Path("{appartenanceId}/get/{contactId}")
    @RequireDroits([Droit.GEST_SITE_R])
    fun getById(
        @PathParam("appartenanceId")
        appartenanceId: UUID,
        @PathParam("contactId")
        contactId: UUID,
    ): Response =
        Response.ok(
            contactRepository.getById(contactId, contactRepository.checkIsGestionnaire(appartenanceId)),
        ).build()

    @DELETE
    @Path("{appartenanceId}/delete/{contactId}")
    @RequireDroits([Droit.GEST_SITE_A])
    fun delete(
        @PathParam("contactId")
        contactId: UUID,
        @PathParam("appartenanceId")
        appartenanceId: UUID,
    ): Response {
        val isGestionnaire = contactRepository.checkIsGestionnaire(appartenanceId)
        return deleteContactUseCase.execute(
            securityContext.userInfo,
            contactRepository.getById(contactId, isGestionnaire).copy(
                isGestionnaire = isGestionnaire,
            ),
        )
            .wrap()
    }

    @GET
    @Path("/fonctions")
    @RequireDroits([Droit.GEST_SITE_A, Droit.ADMIN_DROITS])
    fun getAllFonctions() =
        Response.ok(fonctionContactRepository.getAll()).build()
}
