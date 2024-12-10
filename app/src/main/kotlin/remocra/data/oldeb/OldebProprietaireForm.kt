package remocra.data.oldeb

import remocra.db.jooq.remocra.enums.TypeCivilite
import java.util.UUID

data class OldebProprietaireForm(
    val oldebProprietaireId: UUID = UUID.randomUUID(),
    val oldebProprietaireOrganisme: Boolean,
    val oldebProprietaireRaisonSociale: String?,
    val oldebProprietaireCivilite: TypeCivilite,
    val oldebProprietaireNom: String,
    val oldebProprietairePrenom: String,
    val oldebProprietaireTelephone: String?,
    val oldebProprietaireEmail: String?,
    val oldebProprietaireNumVoie: String?,
    val oldebProprietaireVoie: String?,
    val oldebProprietaireLieuDit: String?,
    val oldebProprietaireCodePostal: String,
    val oldebProprietaireVille: String,
    val oldebProprietairePays: String,
)
