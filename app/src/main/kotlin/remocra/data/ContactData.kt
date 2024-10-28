package remocra.data

import remocra.db.jooq.remocra.enums.TypeCivilite
import remocra.db.jooq.remocra.enums.TypeFonction
import java.util.UUID

data class ContactData(
    val contactId: UUID,
    val appartenanceId: UUID,
    val siteId: UUID?,
    val contactActif: Boolean,
    val contactCivilite: TypeCivilite?,
    val contactFonction: TypeFonction?,
    val contactNom: String?,
    val contactPrenom: String?,
    val contactNumeroVoie: String?,
    val contactSuffixe: String?,
    val contactLieuDitText: String?,
    val contactLieuDitId: UUID?,
    val contactVoieText: String?,
    val contactVoieId: UUID?,
    val contactCommuneId: UUID?,
    val contactCommuneText: String?,
    val contactCodePostal: String?,
    val contactPays: String?,
    val contactTelephone: String?,
    val contactEmail: String?,
    val listRoleId: List<UUID>,
    val isGestionnaire: Boolean = false,
)
