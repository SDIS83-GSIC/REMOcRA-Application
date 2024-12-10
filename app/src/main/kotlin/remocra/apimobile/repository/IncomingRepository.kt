package remocra.apimobile.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.TourneeSynchroForApiMobileData
import remocra.apimobile.data.VisiteForApiMobileData
import remocra.db.AbstractRepository
import remocra.db.fetchOneInto
import remocra.db.jooq.incoming.tables.references.CONTACT
import remocra.db.jooq.incoming.tables.references.GESTIONNAIRE
import remocra.db.jooq.incoming.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.incoming.tables.references.L_VISITE_ANOMALIE
import remocra.db.jooq.incoming.tables.references.NEW_PEI
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
}
