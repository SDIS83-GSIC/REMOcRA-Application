package remocra.apimobile.data

import remocra.db.jooq.remocra.enums.TypeCivilite
import java.util.UUID

class ContactForApiMobileData(
    val contactId: UUID,
    val gestionnaireId: UUID,
    val contactFonctionContactId: UUID?,
    val contactCivilite: TypeCivilite?,
    val contactNom: String?,
    val contactPrenom: String?,

    val contactNumeroVoie: String?,
    val contactSuffixeVoie: String?,
    val contactLieuDitText: String?,
    val contactLieuDitId: UUID?,
    val contactVoieText: String?,
    val contactVoieId: UUID?,
    val contactCodePostal: String?,
    val contactCommuneText: String?,
    val contactCommuneId: UUID?,
    val contactPays: String?,
    val contactTelephone: String?,
    val contactEmail: String?,
)
