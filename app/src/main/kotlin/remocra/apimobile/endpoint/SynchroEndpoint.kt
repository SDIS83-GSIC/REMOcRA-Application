package remocra.apimobile.endpoint

import jakarta.inject.Inject
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.data.NewPeiForMobileApiData
import remocra.apimobile.data.PhotoPeiForApiMobileData
import remocra.apimobile.data.TourneeSynchroForApiMobileData
import remocra.apimobile.data.VisiteAnomalieForApiMobileData
import remocra.apimobile.data.VisiteForApiMobileData
import remocra.apimobile.usecase.SynchroNewPeiUseCase
import remocra.apimobile.usecase.TourneeUseCase
import remocra.apimobile.usecase.synchrofintournee.SynchroFinTourneeUseCase
import remocra.apimobile.usecase.synchrogestionnaire.SynchroContactRoleUseCase
import remocra.apimobile.usecase.synchrogestionnaire.SynchroContactUseCase
import remocra.apimobile.usecase.synchrogestionnaire.SynchroGestionnaireUseCase
import remocra.apimobile.usecase.synchrophotopei.SynchroPhotoPeiUseCase
import remocra.apimobile.usecase.synchrotournee.SynchroTourneeUseCase
import remocra.apimobile.usecase.synchrovisite.SynchroVisiteAnomalieUseCase
import remocra.apimobile.usecase.synchrovisite.SynchroVisiteUseCase
import remocra.app.ParametresProvider
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.userInfo
import remocra.db.jooq.incoming.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.web.AbstractEndpoint
import java.util.UUID

@Path("/mobile/synchro")
@Produces("application/json; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON)
class SynchroEndpoint : AbstractEndpoint() {
    @Inject
    lateinit var tourneeUseCase: TourneeUseCase

    @Inject
    lateinit var synchroGestionnaireUseCase: SynchroGestionnaireUseCase

    @Inject
    lateinit var synchroNewPeiUseCase: SynchroNewPeiUseCase

    @Inject
    lateinit var synchroContactUseCase: SynchroContactUseCase

    @Inject
    lateinit var synchroContactRoleUseCase: SynchroContactRoleUseCase

    @Inject
    lateinit var synchroTourneeUseCase: SynchroTourneeUseCase

    @Inject
    lateinit var synchroVisiteUseCase: SynchroVisiteUseCase

    @Inject
    lateinit var synchroVisiteAnomalieUseCase: SynchroVisiteAnomalieUseCase

    @Inject
    lateinit var synchroPhotoPeiUseCase: SynchroPhotoPeiUseCase

    @Inject
    lateinit var synchroFinTourneeUseCase: SynchroFinTourneeUseCase

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Context
    lateinit var securityContext: SecurityContext

    @GET
    @Path("/tournees-dispos")
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun getTourneesDispos(): Response {
        return Response.ok(tourneeUseCase.getTourneesDisponibles(securityContext.userInfo)).build()
    }

    @Path("/reserver-tournees")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun reserverTournees(
        @FormParam("listIdTournees") listIdTournees: List<UUID>,
    ): Response {
        // On retourne les tournées réservées et celles qu'on n'a pas pu réserver
        return Response.ok(tourneeUseCase.reserveTournees(listIdTournees, securityContext.userInfo.utilisateurId!!))
            .build()
    }

    @Path("/annule-reservation")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @RequireDroits([Droit.TOURNEE_R, Droit.TOURNEE_A])
    fun annuleReservationTournee(@FormParam("idTournee") idTournee: UUID): Response {
        return tourneeUseCase.annuleReservation(idTournee, securityContext.userInfo.utilisateurId!!)
    }

    @Path("/create-pei")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    @RequireDroits([Droit.MOBILE_PEI_C])
    fun createPei(
        @FormParam("peiId") idPei: UUID,
        @FormParam("lat") lat: Double,
        @FormParam("lon") lon: Double,
        @FormParam("peiTypePei") peiTypePei: TypePei,
        @FormParam("gestionnaireId") gestionnaireId: UUID?,
        @FormParam("peiObservation") peiObservation: String?,
        @FormParam("natureDeciId") natureDeciId: UUID,
        @FormParam("natureId") natureId: UUID,
    ) =
        synchroNewPeiUseCase.execute(
            securityContext.userInfo,
            NewPeiForMobileApiData(
                idPei,
                gestionnaireId,
                natureId,
                natureDeciId,
                lon,
                lat,
                peiTypePei,
                peiObservation,
            ),
        ).wrap()

    @Path("/gestionnaires")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RequireDroits([Droit.MOBILE_GESTIONNAIRE_C])
    fun getGestionnaire(
        @FormParam("gestionnaireId") gestionnaireId: UUID,
        @FormParam("gestionnaireLibelle") gestionnaireLibelle: String,
        @FormParam("gestionnaireCode") gestionnaireCode: String,
    ): Response {
        return synchroGestionnaireUseCase.execute(
            securityContext.userInfo,
            Gestionnaire(
                gestionnaireId = gestionnaireId,
                gestionnaireCode = gestionnaireCode,
                gestionnaireLibelle = gestionnaireLibelle,
            ),
        ).wrap()
    }

    @Path("/contacts")
    @POST
    @RequireDroits([Droit.MOBILE_GESTIONNAIRE_C])
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun getContact(
        @FormParam("contactId") contactId: UUID,
        @FormParam("gestionnaireId") gestionnaireId: UUID,
        @FormParam("contactNom") contactNom: String?,
        @FormParam("contactPrenom") contactPrenom: String?,
        @FormParam("contactFonctionContactId") contactFonctionContactId: UUID?,
        @FormParam("contactCivilite") contactCivilite: TypeCivilite?,
        @FormParam("contactCodePostal") contactCodePostal: String?,
        @FormParam("contactVoieText") contactVoieText: String?,
        @FormParam("contactSuffixeVoie") contactSuffixeVoie: String?,
        @FormParam("contactNumeroVoie") contactNumeroVoie: String?,
        @FormParam("contactLieuDitText") contactLieuDitText: String?,
        @FormParam("contactTelephone") contactTelephone: String?,
        @FormParam("contactEmail") contactEmail: String?,
        @FormParam("contactCommuneText") contactCommuneText: String?,
        @FormParam("contactPays") contactPays: String?,
    ) =
        // TODO voir pour les id commune, voie et lieu dit
        synchroContactUseCase.execute(
            securityContext.userInfo,
            ContactForApiMobileData(
                contactId = contactId,
                contactActif = true,
                gestionnaireId = gestionnaireId,
                contactFonctionContactId = contactFonctionContactId,
                contactCivilite = contactCivilite,
                contactNom = contactNom,
                contactPrenom = contactPrenom,
                contactNumeroVoie = contactNumeroVoie,
                contactSuffixeVoie = contactSuffixeVoie,
                contactLieuDitText = contactLieuDitText,
                contactLieuDitId = null,
                contactVoieText = contactVoieText,
                contactVoieId = null,
                contactCodePostal = contactCodePostal,
                contactCommuneText = contactCommuneText,
                contactCommuneId = null,
                contactPays = contactPays,
                contactTelephone = contactTelephone,
                contactEmail = contactEmail,
            ),
        ).wrap()

    @Path("/contacts-roles")
    @POST
    @RequireDroits([Droit.MOBILE_GESTIONNAIRE_C])
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun getContactRole(
        @FormParam("contactId") contactId: UUID,
        @FormParam("roleId") roleId: UUID,
    ) = synchroContactRoleUseCase.execute(
        securityContext.userInfo,
        ContactRoleForApiMobileData(
            contactId = contactId,
            roleContactId = roleId,
        ),
    ).wrap()

    @Path("/synchro-tournee")
    @Public("Tous les utilisateurs connectés peuvent synchroniser les données")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun synchroTournee(
        @FormParam("tourneeId")
        tourneeId: UUID,
        @FormParam("tourneeLibelle")
        tourneeLibelle: String?,
    ): Response =
        synchroTourneeUseCase.execute(
            securityContext.userInfo,
            TourneeSynchroForApiMobileData(
                tourneeId,
                tourneeLibelle,
            ),
        ).wrap()

    @Path("/synchro-visite")
    @POST
    @Public("Tous les utilisateurs connectés peuvent synchroniser les données")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun synchroVisite(
        @FormParam("visiteId") visiteId: UUID,
        @FormParam("tourneeId") tourneeId: UUID,
        @FormParam("peiId") peiId: UUID,
        @FormParam("visiteDate") visiteDate: String,
        @FormParam("visiteTypeVisite") visiteTypeVisite: TypeVisite,
        @FormParam("ctrDebitPression") ctrDebitPression: Boolean,
        @FormParam("visiteAgent1") visiteAgent1: String?,
        @FormParam("visiteAgent2") visiteAgent2: String?,
        @FormParam("visiteCtrlDebitPressionDebit") visiteCtrlDebitPressionDebit: Int?,
        @FormParam("visiteCtrlDebitPressionPression") visiteCtrlDebitPressionPression: Double?,
        @FormParam("visiteCtrlDebitPressionPressionDyn") visiteCtrlDebitPressionPressionDyn: Double?,
        @FormParam("visiteObservations") visiteObservations: String?,
        @FormParam("hasAnomalieChanges") hasAnomalieChanges: Boolean,
    ) = synchroVisiteUseCase.execute(
        userInfo = securityContext.userInfo,
        element = VisiteForApiMobileData(
            visiteId = visiteId,
            tourneeId = tourneeId,
            peiId = peiId,
            visiteDate = visiteDate,
            visiteTypeVisite = visiteTypeVisite,
            ctrDebitPression = ctrDebitPression,
            visiteAgent1 = visiteAgent1,
            visiteAgent2 = visiteAgent2,
            visiteCtrlDebitPressionDebit = visiteCtrlDebitPressionDebit,
            visiteCtrlDebitPressionPression = visiteCtrlDebitPressionPression,
            visiteCtrlDebitPressionPressionDyn = visiteCtrlDebitPressionPressionDyn,
            visiteObservations = visiteObservations,
            hasAnomalieChanges = hasAnomalieChanges,
        ),
    ).wrap()

    @Path("/synchro-visite-anomalie")
    @POST
    @Public("Tous les utilisateurs connectés peuvent synchroniser les données")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun synchroHydrantVisiteAnomalies(
        @FormParam("visiteId") visiteId: UUID,
        @FormParam("anomalieId") anomalieId: UUID,
    ): Response =
        synchroVisiteAnomalieUseCase.execute(
            securityContext.userInfo,
            VisiteAnomalieForApiMobileData(
                visiteId,
                anomalieId,
            ),
        ).wrap()

    @Path("/synchro-photo")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Public("Tous les utilisateurs connectés peuvent synchroniser les données")
    fun synchroPhotoPei(
        @Context httpServletRequest: HttpServletRequest,
    ): Response {
        val partPhoto: Part = httpServletRequest.getPart("photo")
        val photoBytes: ByteArray = partPhoto.inputStream.readAllBytes()

        return synchroPhotoPeiUseCase.execute(
            securityContext.userInfo,
            PhotoPeiForApiMobileData(
                photoId = UUID.fromString(httpServletRequest.getPart("photoId").inputStream.reader().readText()),
                peiId = UUID.fromString(httpServletRequest.getPart("peiId").inputStream.reader().readText()),
                photoDate = httpServletRequest.getPart("photoDate").inputStream.reader().readText(),
                photoInputStream = photoBytes,
                photoLibelle = partPhoto.submittedFileName,
            ),
        ).wrap()
    }

    @Path("/incoming-to-remocra/{tourneeId}")
    @POST
    @RequireDroits([Droit.PEI_R])
    fun endSynchroTournee(
        @PathParam("tourneeId")
        tourneeId: UUID,
    ): Response {
        return Response.ok(synchroFinTourneeUseCase.execute(tourneeId, securityContext.userInfo)).build()
    }
}
