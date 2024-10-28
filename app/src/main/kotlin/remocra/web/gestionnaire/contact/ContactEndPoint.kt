package remocra.web.gestionnaire.contact

import com.google.inject.Inject
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.POST
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
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypeFonction
import remocra.usecase.gestionnaire.CreateContactUseCase
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/contact")
@Produces(MediaType.APPLICATION_JSON)
class ContactEndPoint : AbstractEndpoint() {

    @Inject
    lateinit var createContactUseCase: CreateContactUseCase

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
    ): Response =
        createContactUseCase.execute(
            securityContext.userInfo,
            ContactData(
                contactId = UUID.randomUUID(),
                appartenanceId = appartenanceId,
                siteId = contactInput.siteId,
                contactActif = contactInput.contactActif,
                contactCivilite = contactInput.contactCivilite,
                contactFonction = contactInput.contactFonction,
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
            ),
        ).wrap()

    class ContactInput {
        @FormParam("contactActif")
        var contactActif: Boolean = true

        @FormParam("contactCivilite")
        var contactCivilite: TypeCivilite? = null

        @FormParam("contactFonction")
        var contactFonction: TypeFonction? = null

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
}
