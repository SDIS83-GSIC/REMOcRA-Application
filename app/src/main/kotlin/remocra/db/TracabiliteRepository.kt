package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.JSONB
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.historique.tables.references.TRACABILITE
import java.time.OffsetDateTime
import java.util.UUID

class TracabiliteRepository @Inject constructor(private val dsl: DSLContext) {

    fun insertTracabilite(
        typeOperation: TypeOperation,
        typeObjet: TypeObjet,
        objetId: UUID,
        objetData: JSONB,
        auteurId: UUID,
        auteurData: JSONB,
        date: OffsetDateTime,
    ) =
        dsl.insertInto(TRACABILITE)
            .set(TRACABILITE.ID, UUID.randomUUID())
            .set(TRACABILITE.TYPE_OPERATION, typeOperation)
            .set(TRACABILITE.DATE, date)
            .set(TRACABILITE.OBJET_ID, objetId)
            .set(TRACABILITE.TYPE_OBJET, typeObjet)
            .set(TRACABILITE.OBJET_DATA, objetData)
            .set(TRACABILITE.AUTEUR_ID, auteurId)
            .set(TRACABILITE.AUTEUR_DATA, auteurData)
            .execute()
}
