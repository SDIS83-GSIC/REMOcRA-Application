package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.references.L_SIGNALEMENT_ELEMENT_SIGNALEMENT_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_ELEMENT
import remocra.db.jooq.remocra.tables.references.SIGNALEMENT_TYPE_ANOMALIE
import java.util.UUID

class SignalementElementRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insertSignalementElement(signalementElement: remocra.db.jooq.remocra.tables.pojos.SignalementElement) =
        dsl.insertInto(SIGNALEMENT_ELEMENT).set(dsl.newRecord(SIGNALEMENT_ELEMENT, signalementElement)).execute()

    fun insertLiaisonAnomalie(signalementElementId: UUID, listeAnomalie: Collection<String>) {
        listeAnomalie.forEach { anomalie ->
            dsl.insertInto(L_SIGNALEMENT_ELEMENT_SIGNALEMENT_TYPE_ANOMALIE)
                .set(L_SIGNALEMENT_ELEMENT_SIGNALEMENT_TYPE_ANOMALIE.ELEMENT_ID, signalementElementId)
                .set(
                    L_SIGNALEMENT_ELEMENT_SIGNALEMENT_TYPE_ANOMALIE.SIGNALEMENT_TYPE_ANOMALIE_ID,
                    dsl.select(SIGNALEMENT_TYPE_ANOMALIE.ID).from(
                        SIGNALEMENT_TYPE_ANOMALIE,
                    ).where(SIGNALEMENT_TYPE_ANOMALIE.CODE.eq(anomalie)),
                )
                .execute()
        }
    }
}
