package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Contact
import remocra.db.jooq.remocra.tables.pojos.LContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LContactRole
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE

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
}
