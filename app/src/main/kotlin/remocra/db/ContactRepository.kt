package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.ContactData
import remocra.data.Params
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.LContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactOrganisme
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.SITE
import java.util.UUID

class ContactRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun insertContact(contact: Contact) =
        dsl.insertInto(CONTACT)
            .set(dsl.newRecord(CONTACT, contact))
            .execute()

    fun updateContact(contact: Contact) =
        dsl.update(CONTACT)
            .set(dsl.newRecord(CONTACT, contact))
            .where(CONTACT.ID.eq(contact.contactId))
            .execute()

    fun updateSite(contactId: UUID, siteId: UUID?) =
        dsl.update(L_CONTACT_GESTIONNAIRE)
            .set(SITE.ID, siteId)
            .where(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(contactId))
            .execute()

    fun insertLContactGestionnaire(lContactGestionnaire: LContactGestionnaire) =
        dsl.insertInto(L_CONTACT_GESTIONNAIRE)
            .set(dsl.newRecord(L_CONTACT_GESTIONNAIRE, lContactGestionnaire))
            .execute()

    fun insertLContactOrganisme(lContactOrganisme: LContactOrganisme) =
        dsl.insertInto(L_CONTACT_ORGANISME)
            .set(dsl.newRecord(L_CONTACT_ORGANISME, lContactOrganisme))
            .execute()

    fun insertLContactRole(lContactRole: LContactRole) =
        dsl.insertInto(L_CONTACT_ROLE)
            .set(dsl.newRecord(L_CONTACT_ROLE, lContactRole))
            .execute()

    fun deleteLContactRole(contactId: UUID) =
        dsl.delete(L_CONTACT_ROLE)
            .where(L_CONTACT_ROLE.CONTACT_ID.eq(contactId))
            .execute()

    fun deleteLContactGestionnaire(contactId: UUID) =
        dsl.delete(L_CONTACT_GESTIONNAIRE)
            .where(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(contactId))
            .execute()
    fun deleteLContactOrganisme(contactId: UUID) =
        dsl.delete(L_CONTACT_ORGANISME)
            .where(L_CONTACT_ORGANISME.CONTACT_ID.eq(contactId))
            .execute()

    fun deleteContact(contactId: UUID) =
        dsl.delete(CONTACT)
            .where(CONTACT.ID.eq(contactId))
            .execute()

    fun getAllForAdmin(params: Params<Filter, Sort>, appartenanceId: UUID, isGestionnaire: Boolean): Collection<ContactWithSite> =
        dsl.select(
            CONTACT.ID,
            CONTACT.CIVILITE,
            CONTACT.ACTIF,
            CONTACT.NOM,
            CONTACT.PRENOM,
            FONCTION_CONTACT.ID,
            FONCTION_CONTACT.LIBELLE,
            FONCTION_CONTACT.ID,
            CONTACT.TELEPHONE,
            CONTACT.EMAIL,
        )
            .let {
                if (isGestionnaire) {
                    it.select(SITE.LIBELLE)
                }
                it
            }
            .from(CONTACT)
            .let {
                if (isGestionnaire) {
                    it.join(L_CONTACT_GESTIONNAIRE)
                        .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
                        .leftJoin(SITE)
                        .on(SITE.ID.eq(L_CONTACT_GESTIONNAIRE.SITE_ID))
                } else {
                    it.join(L_CONTACT_ORGANISME)
                        .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
                }
            }
            .leftJoin(FONCTION_CONTACT)
            .on(FONCTION_CONTACT.ID.eq(CONTACT.FONCTION_CONTACT_ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .let {
                if (isGestionnaire) {
                    it.and(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.eq(appartenanceId))
                } else {
                    it.and(L_CONTACT_ORGANISME.ORGANISME_ID.eq(appartenanceId))
                }
            }
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(CONTACT.NOM))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?, appartenanceId: UUID, isGestionnaire: Boolean) =
        dsl.select(CONTACT.ID)
            .from(CONTACT)
            .leftJoin(FONCTION_CONTACT)
            .on(FONCTION_CONTACT.ID.eq(CONTACT.FONCTION_CONTACT_ID))
            .let {
                if (isGestionnaire) {
                    it.join(L_CONTACT_GESTIONNAIRE)
                        .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
                        .leftJoin(SITE)
                        .on(SITE.ID.eq(L_CONTACT_GESTIONNAIRE.SITE_ID))
                        .where(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.eq(appartenanceId))
                } else {
                    it.join(L_CONTACT_ORGANISME)
                        .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
                        .where(L_CONTACT_ORGANISME.ORGANISME_ID.eq(appartenanceId))
                }
            }
            .and(filterBy?.toCondition() ?: DSL.trueCondition())
            .count()

    data class ContactWithSite(
        val contactId: UUID,
        val contactActif: Boolean,
        val contactCivilite: TypeCivilite?,
        val contactNom: String?,
        val contactPrenom: String?,
        val fonctionContactLibelle: String?,
        val fonctionContactId: UUID?,
        val contactTelephone: String?,
        val contactEmail: String?,
        val siteLibelle: String?,
        val siteId: UUID?,
    )

    data class Filter(
        val contactCivilite: TypeCivilite?,
        val contactActif: Boolean?,
        val contactNom: String?,
        val contactPrenom: String?,
        val fonctionContactLibelle: String?,
        val contactTelephone: String?,
        val contactEmail: String?,
        val siteLibelle: String?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    contactCivilite?.let { DSL.and(CONTACT.CIVILITE.eq(it)) },
                    contactActif?.let { DSL.and(CONTACT.ACTIF.eq(it)) },
                    contactNom?.let { DSL.and(CONTACT.NOM.contains(it)) },
                    contactPrenom?.let { DSL.and(CONTACT.PRENOM.contains(it)) },
                    fonctionContactLibelle?.let { DSL.and(FONCTION_CONTACT.LIBELLE.contains(it)) },
                    contactTelephone?.let { DSL.and(CONTACT.TELEPHONE.contains(it)) },
                    contactEmail?.let { DSL.and(CONTACT.EMAIL.contains(it)) },
                    siteLibelle?.let { DSL.and(SITE.LIBELLE.contains(it)) },
                ),
            )
    }

    data class Sort(
        val contactCivilite: Int?,
        val contactActif: Int?,
        val contactNom: Int?,
        val contactPrenom: Int?,
        val contactFonctionLibelle: Int?,
        val contactTelephone: Int?,
        val contactEmail: Int?,
        val siteLibelle: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            CONTACT.CIVILITE.getSortField(contactCivilite),
            CONTACT.ACTIF.getSortField(contactActif),
            CONTACT.NOM.getSortField(contactNom),
            CONTACT.PRENOM.getSortField(contactPrenom),
            FONCTION_CONTACT.LIBELLE.getSortField(contactFonctionLibelle),
            CONTACT.TELEPHONE.getSortField(contactTelephone),
            CONTACT.EMAIL.getSortField(contactEmail),
            SITE.LIBELLE.getSortField(siteLibelle),
        )
    }

    fun getById(contactId: UUID, isGestionnaire: Boolean): ContactData =
        dsl.select(
            *CONTACT.fields(),
            multiset(
                selectDistinct(L_CONTACT_ROLE.ROLE_ID)
                    .from(L_CONTACT_ROLE)
                    .where(L_CONTACT_ROLE.CONTACT_ID.eq(CONTACT.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1().let { it as UUID }
                }
            }.`as`("listRoleId"),
        )
            .let {
                if (isGestionnaire) {
                    it.select(
                        L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.`as`("appartenanceId"),
                        L_CONTACT_GESTIONNAIRE.SITE_ID.`as`("siteId"),
                    )
                } else {
                    it.select(L_CONTACT_ORGANISME.ORGANISME_ID.`as`("appartenanceId"))
                }
            }
            .from(CONTACT)
            .let {
                if (isGestionnaire) {
                    it.join(L_CONTACT_GESTIONNAIRE)
                        .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
                } else {
                    it.join(L_CONTACT_ORGANISME)
                        .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
                }
            }
            .where(CONTACT.ID.eq(contactId))
            .fetchSingleInto()

    fun checkIsGestionnaire(appartenanceId: UUID) =
        dsl.fetchExists(dsl.select(GESTIONNAIRE.ID).from(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(appartenanceId)))

    fun getContactWithGestionnaires(listeGestionnaireId: Collection<UUID>): Collection<UUID> =
        dsl.select(L_CONTACT_GESTIONNAIRE.CONTACT_ID)
            .from(L_CONTACT_GESTIONNAIRE)
            .where(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.`in`(listeGestionnaireId))
            .fetchInto()
}
