package remocra.apimobile.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.TourneeSynchroForApiMobileData
import remocra.apimobile.data.VisiteForApiMobileData
import remocra.db.AbstractRepository
import remocra.db.fetchInto
import remocra.db.fetchOneInto
import remocra.db.jooq.incoming.tables.pojos.Contact
import remocra.db.jooq.incoming.tables.pojos.Gestionnaire
import remocra.db.jooq.incoming.tables.pojos.LContactRole
import remocra.db.jooq.incoming.tables.pojos.LVisiteAnomalie
import remocra.db.jooq.incoming.tables.pojos.NewPei
import remocra.db.jooq.incoming.tables.pojos.PhotoPei
import remocra.db.jooq.incoming.tables.pojos.Visite
import remocra.db.jooq.incoming.tables.pojos.VisiteCtrlDebitPression
import remocra.db.jooq.incoming.tables.references.CONTACT
import remocra.db.jooq.incoming.tables.references.GESTIONNAIRE
import remocra.db.jooq.incoming.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.incoming.tables.references.L_VISITE_ANOMALIE
import remocra.db.jooq.incoming.tables.references.NEW_PEI
import remocra.db.jooq.incoming.tables.references.PHOTO_PEI
import remocra.db.jooq.incoming.tables.references.TOURNEE
import remocra.db.jooq.incoming.tables.references.VISITE
import remocra.db.jooq.incoming.tables.references.VISITE_CTRL_DEBIT_PRESSION
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.utils.ST_SetSrid
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import remocra.utils.toGeomFromText
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

class IncomingRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    /** Crée un PEI  */
    fun insertPei(
        peiId: UUID,
        gestionnaireId: UUID?,
        communeId: UUID,
        natureId: UUID,
        natureDeciId: UUID,
        peiTypePei: TypePei,
        peiObservation: String?,
        peiGeometrie: Geometry,
        peiVoieId: UUID?,
        srid: Int,
    ): Int =
        dsl
            .insertInto(NEW_PEI)
            .set(NEW_PEI.ID, peiId)
            .set(
                NEW_PEI.GEOMETRIE,
                ST_Transform(
                    ST_SetSrid(
                        peiGeometrie.toGeomFromText(),
                        GlobalConstants.SRID_4326,
                    ),
                    srid,
                ),

            )
            .set(NEW_PEI.TYPE_PEI, peiTypePei)
            .set(NEW_PEI.COMMUNE_ID, communeId)
            .set(NEW_PEI.VOIE_ID, peiVoieId)
            .set(NEW_PEI.NATURE_ID, natureDeciId)
            .set(NEW_PEI.NATURE_ID, natureId)
            .set(NEW_PEI.GESTIONNAIRE_ID, gestionnaireId)
            .set(NEW_PEI.OBSERVATION, peiObservation)
            .onConflictDoNothing()
            .execute()

    /**
     * Permet de récupérer l'id de la commune en fonction de la géométrie du PEI
     *
     * @param geometrie
     * @return
     */
    fun getCommuneWithGeometrie(geometrie: Geometry, srid: Int): UUID? =
        dsl
            .select(COMMUNE.ID)
            .from(COMMUNE)
            .where(
                ST_Within(
                    ST_Transform(
                        ST_SetSrid(
                            geometrie.toGeomFromText(),
                            srid = GlobalConstants.SRID_4326,
                        ),
                        srid = srid,
                    ),
                    COMMUNE.GEOMETRIE,
                ),
            )
            .fetchOneInto()

    fun getVoie(geometrie: Geometry, srid: Int): UUID? =
        dsl
            .select(VOIE.LIBELLE)
            .from(VOIE)
            .where(
                ST_Within(
                    ST_Transform(
                        ST_SetSrid(
                            geometrie.toGeomFromText(),
                            srid = GlobalConstants.SRID_4326,
                        ),
                        srid = srid,
                    ),
                    VOIE.GEOMETRIE,
                ),
            )
            .fetchOneInto()

    fun insertGestionnaireIncoming(
        gestionnaireCode: String?,
        gestionnaireLibelle: String?,
        gestionnaireId: UUID,
    ) =
        dsl
            .insertInto(GESTIONNAIRE)
            .set(GESTIONNAIRE.ID, gestionnaireId)
            .set(GESTIONNAIRE.CODE, gestionnaireCode)
            .set(GESTIONNAIRE.LIBELLE, gestionnaireLibelle)
            .onConflictDoNothing()
            .execute()

    fun checkGestionnaireExist(gestionnaireId: UUID) =
        dsl.fetchExists(
            dsl.selectFrom<Record>(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(gestionnaireId)),
        )

    fun insertContact(contact: ContactForApiMobileData): Int =
        dsl
            .insertInto(CONTACT)
            .set(CONTACT.ID, contact.contactId)
            .set(CONTACT.GESTIONNAIRE_ID, contact.gestionnaireId)
            .set(CONTACT.FONCTION_CONTACT_ID, contact.contactFonctionContactId)
            .set(CONTACT.CIVILITE, contact.contactCivilite)
            .set(CONTACT.NOM, contact.contactNom)
            .set(CONTACT.PRENOM, contact.contactPrenom)
            .set(CONTACT.CODE_POSTAL, contact.contactCodePostal)
            .set(CONTACT.COMMUNE_TEXT, contact.contactCommuneText)
            .set(CONTACT.NUMERO_VOIE, contact.contactNumeroVoie)
            .set(CONTACT.SUFFIXE_VOIE, contact.contactSuffixeVoie)
            .set(CONTACT.VOIE_TEXT, contact.contactVoieText)
            .set(CONTACT.LIEU_DIT_TEXT, contact.contactLieuDitText)
            .set(CONTACT.PAYS, contact.contactPays)
            .set(CONTACT.EMAIL, contact.contactEmail)
            .set(CONTACT.TELEPHONE, contact.contactTelephone)
            .onConflictDoNothing()
            .execute()

    fun checkContactExist(idContact: UUID?): Boolean {
        return dsl.fetchExists(dsl.selectFrom(CONTACT).where(CONTACT.ID.eq(idContact)))
    }

    fun insertContactRole(contactId: UUID, roleId: UUID): Int {
        return dsl
            .insertInto(L_CONTACT_ROLE)
            .set(L_CONTACT_ROLE.CONTACT_ID, contactId)
            .set(L_CONTACT_ROLE.ROLE_ID, roleId)
            .onConflictDoNothing()
            .execute()
    }

    fun insertTournee(tourneeData: TourneeSynchroForApiMobileData): Int =
        dsl
            .insertInto(TOURNEE)
            .set(TOURNEE.ID, tourneeData.tourneeId)
            .set(TOURNEE.LIBELLE, tourneeData.tourneeLibelle)
            .set(TOURNEE.DATE_DEBUT_SYNCHRO, dateUtils.now())
            .onConflictDoNothing()
            .execute()

    fun insertVisite(visiteData: VisiteForApiMobileData, date: ZonedDateTime): Int =
        dsl
            .insertInto(VISITE)
            .set(VISITE.ID, visiteData.visiteId)
            .set(VISITE.TYPE_VISITE, visiteData.visiteTypeVisite)
            .set(VISITE.AGENT1, visiteData.visiteAgent1)
            .set(VISITE.AGENT2, visiteData.visiteAgent2)
            .set(VISITE.DATE, date)
            .set(VISITE.TOURNEE_ID, visiteData.tourneeId)
            .set(VISITE.OBSERVATION, visiteData.visiteObservations)
            .set(VISITE.HAS_ANOMALIE_CHANGES, visiteData.hasAnomalieChanges)
            .onConflictDoNothing()
            .execute()

    fun insertVisiteCtrlDebitPression(visiteData: VisiteForApiMobileData): Int =
        dsl
            .insertInto(VISITE_CTRL_DEBIT_PRESSION)
            .set(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID, visiteData.visiteId)
            .set(VISITE_CTRL_DEBIT_PRESSION.DEBIT, visiteData.visiteCtrlDebitPressionDebit)
            .set(VISITE_CTRL_DEBIT_PRESSION.PRESSION, visiteData.visiteCtrlDebitPressionPression.toBigDecimal())
            .set(VISITE_CTRL_DEBIT_PRESSION.PRESSION_DYN, visiteData.visiteCtrlDebitPressionPressionDyn.toBigDecimal())
            .onConflictDoNothing()
            .execute()

    fun insertVisiteAnomalie(visiteId: UUID, anomalieId: UUID): Int =
        dsl
            .insertInto(L_VISITE_ANOMALIE)
            .set(L_VISITE_ANOMALIE.VISITE_ID, visiteId)
            .set(L_VISITE_ANOMALIE.ANOMALIE_ID, anomalieId)
            .onConflictDoNothing()
            .execute()

    fun insertPhotoPei(peiId: UUID, photoId: UUID, photoDate: ZonedDateTime, photoPath: String, photoLibelle: String): Int =
        dsl
            .insertInto(PHOTO_PEI)
            .set(PHOTO_PEI.PHOTO_ID, photoId)
            .set(PHOTO_PEI.PEI_ID, peiId)
            .set(PHOTO_PEI.PHOTO_DATE, photoDate)
            .set(PHOTO_PEI.PHOTO_PATH, photoPath)
            .set(PHOTO_PEI.PHOTO_LIBELLE, photoLibelle)
            .onConflictDoNothing()
            .execute()

    fun getGestionnaires(): Collection<Gestionnaire> =
        dsl.selectFrom(GESTIONNAIRE).fetchInto()

    fun getContacts(): Collection<Contact> =
        dsl.selectFrom(CONTACT).fetchInto()

    fun getContactRole(): Collection<LContactRole> =
        dsl.selectFrom(L_CONTACT_ROLE).fetchInto()

    fun getNewPei(): Collection<NewPei> =
        dsl.selectFrom(NEW_PEI).fetchInto()

    fun getVisites(tourneeId: UUID): Collection<Visite> =
        dsl.selectFrom(VISITE).where(VISITE.TOURNEE_ID.eq(tourneeId)).fetchInto()

    fun getPhotoPei(tourneeId: UUID): Collection<PhotoPei> =
        dsl.select(PHOTO_PEI.fields().asList())
            .from(PHOTO_PEI)
            .join(VISITE)
            .on(VISITE.PEI_ID.eq(PHOTO_PEI.PEI_ID))
            .where(VISITE.TOURNEE_ID.eq(tourneeId)).fetchInto()

    fun getVisitesCtrlDebitPression(tourneeId: UUID): Collection<VisiteCtrlDebitPression> =
        dsl.select(VISITE_CTRL_DEBIT_PRESSION.fields().asList())
            .from(VISITE_CTRL_DEBIT_PRESSION)
            .join(VISITE)
            .on(VISITE.ID.eq(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID))
            .where(VISITE.TOURNEE_ID.eq(tourneeId)).fetchInto()

    fun getVisitesAnomalie(tourneeId: UUID): Collection<LVisiteAnomalie> =
        dsl.select(L_VISITE_ANOMALIE.fields().asList())
            .from(L_VISITE_ANOMALIE)
            .join(VISITE)
            .on(VISITE.ID.eq(L_VISITE_ANOMALIE.VISITE_ID))
            .where(VISITE.TOURNEE_ID.eq(tourneeId)).fetchInto()

    fun deleteNewPei(listeNewPeiId: Collection<UUID>) =
        dsl.deleteFrom(NEW_PEI)
            .where(NEW_PEI.ID.`in`(listeNewPeiId))
            .execute()

    fun deleteVisiteAnomalie(listeVisiteId: Collection<UUID>) =
        dsl.deleteFrom(L_VISITE_ANOMALIE)
            .where(L_VISITE_ANOMALIE.VISITE_ID.`in`(listeVisiteId))
            .execute()

    fun deleteVisite(listeVisiteId: Collection<UUID>) =
        dsl.deleteFrom(VISITE)
            .where(VISITE.ID.`in`(listeVisiteId))
            .execute()

    fun deleteVisiteCtrlDebitPression(listeVisiteId: Collection<UUID>) =
        dsl.deleteFrom(VISITE_CTRL_DEBIT_PRESSION)
            .where(VISITE_CTRL_DEBIT_PRESSION.VISITE_ID.`in`(listeVisiteId))
            .execute()

    fun deleteContact(listeContactId: Collection<UUID>) =
        dsl.deleteFrom(CONTACT)
            .where(CONTACT.ID.`in`(listeContactId))
            .execute()

    fun deleteContactRole(listeContactId: Collection<UUID>) =
        dsl.deleteFrom(L_CONTACT_ROLE)
            .where(L_CONTACT_ROLE.CONTACT_ID.`in`(listeContactId))
            .execute()

    fun deleteGestionnaire(listeGestionnaireId: Collection<UUID>) =
        dsl.deleteFrom(GESTIONNAIRE)
            .where(GESTIONNAIRE.ID.`in`(listeGestionnaireId))
            .execute()

    fun deletePhotoPei(listePhotoPeiId: Collection<UUID>) =
        dsl.deleteFrom(PHOTO_PEI)
            .where(PHOTO_PEI.PHOTO_ID.`in`(listePhotoPeiId))
            .execute()

    fun updateDateSynchroFin(date: ZonedDateTime, tourneeId: UUID) =
        dsl.update(TOURNEE)
            .set(TOURNEE.DATE_FIN_SYNCHRO, date)
            .where(TOURNEE.ID.eq(tourneeId))
            .execute()
}
