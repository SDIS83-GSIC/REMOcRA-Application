package remocra.data

import remocra.db.jooq.remocra.enums.TypeResumeElement
import java.util.UUID

data class FicheResumeBlocData(
    val ficheResumeBlocId: UUID?,
    val ficheResumeBlocTypeResumeData: TypeResumeElement,
    val ficheResumeBlocTitre: String,
    val ficheResumeBlocColonne: Int,
    val ficheResumeBlocLigne: Int,
)
