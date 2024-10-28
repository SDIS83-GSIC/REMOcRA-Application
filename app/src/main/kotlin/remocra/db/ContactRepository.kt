package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.GestionnaireRepository.Filter
import remocra.db.GestionnaireRepository.Sort
import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypeFonction
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.LContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.SITE
import java.util.UUID

class ContactRepository @Inject constructor(private val dsl: DSLContext) {
    fun insertContact(contact: Contact) =
        dsl.insertInto(CONTACT)
            .set(dsl.newRecord(CONTACT, contact))
            .execute()

    fun insertLContactGestionnaire(lContactGestionnaire: LContactGestionnaire) =
        dsl.insertInto(L_CONTACT_GESTIONNAIRE)
            .set(dsl.newRecord(L_CONTACT_GESTIONNAIRE, lContactGestionnaire))
            .execute()

    fun insertLContactRole(lContactRole: LContactRole) =
        dsl.insertInto(L_CONTACT_ROLE)
            .set(dsl.newRecord(L_CONTACT_ROLE, lContactRole))
            .execute()

    fun getAllForAdmin(params: Params<Filter, Sort>, gestionnaireId: UUID): Collection<ContactWithSite> =
        dsl.select(
            CONTACT.ID,
            CONTACT.CIVILITE,
            CONTACT.ACTIF,
            CONTACT.NOM,
            CONTACT.PRENOM,
            CONTACT.FONCTION,
            CONTACT.TELEPHONE,
            CONTACT.EMAIL,
            SITE.LIBELLE,
        )
            .from(CONTACT)
            .join(L_CONTACT_GESTIONNAIRE)
            .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
            .leftJoin(SITE)
            .on(SITE.ID.eq(L_CONTACT_GESTIONNAIRE.SITE_ID))
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .and(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.eq(gestionnaireId))
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(CONTACT.NOM))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?, gestionnaireId: UUID) =
        dsl.select(CONTACT.ID)
            .from(CONTACT)
            .join(L_CONTACT_GESTIONNAIRE)
            .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
            .leftJoin(SITE)
            .on(SITE.ID.eq(L_CONTACT_GESTIONNAIRE.SITE_ID))
            .where(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.eq(gestionnaireId))
            .and(filterBy?.toCondition() ?: DSL.trueCondition())
            .count()

    data class ContactWithSite(
        val contactId: UUID,
        val contactActif: Boolean,
        val contactCivilite: TypeCivilite?,
        val contactNom: String?,
        val contactPrenom: String?,
        val contactFonction: TypeFonction?,
        val contactTelephone: String?,
        val contactEmail: String?,
        val siteLibelle: String?,
    )

    data class Filter(
        val contactCivilite: TypeCivilite?,
        val contactActif: Boolean?,
        val contactNom: String?,
        val contactPrenom: String?,
        val contactFonction: TypeFonction?,
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
                    contactFonction?.let { DSL.and(CONTACT.FONCTION.eq(it)) },
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
        val contactFonction: Int?,
        val contactTelephone: Int?,
        val contactEmail: Int?,
        val siteLibelle: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            CONTACT.CIVILITE.getSortField(contactCivilite),
            CONTACT.ACTIF.getSortField(contactActif),
            CONTACT.NOM.getSortField(contactNom),
            CONTACT.PRENOM.getSortField(contactPrenom),
            CONTACT.FONCTION.getSortField(contactFonction),
            CONTACT.TELEPHONE.getSortField(contactTelephone),
            CONTACT.EMAIL.getSortField(contactEmail),
            SITE.LIBELLE.getSortField(siteLibelle),
        )
    }
}
