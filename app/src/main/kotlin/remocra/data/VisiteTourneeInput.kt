package remocra.data

import jakarta.ws.rs.FormParam
import remocra.db.jooq.remocra.enums.TypeVisite
import java.time.ZonedDateTime

class VisiteTourneeInput {
    @FormParam("visiteDate")
    lateinit var visiteDate: ZonedDateTime

    @FormParam("visiteTypeVisite")
    lateinit var visiteTypeVisite: TypeVisite

    @FormParam("visiteAgent1")
    lateinit var visiteAgent1: String

    @FormParam("visiteAgent2")
    val visiteAgent2: String? = null

    @FormParam("isCtrlDebitPression")
    val isCtrlDebitPression: Boolean = false

    @FormParam("listeSimplifiedVisite")
    val listeSimplifiedVisite: List<SimplifiedVisiteInput>? = null
}
