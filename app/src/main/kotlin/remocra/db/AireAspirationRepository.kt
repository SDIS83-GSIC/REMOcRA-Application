package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.AireAspirationUpsertData
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.PenaAspiration
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.TYPE_PENA_ASPIRATION
import java.util.UUID

class AireAspirationRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAiresAspiration(penaId: UUID): Collection<AireAspirationUpsertData> =
        dsl.select(
            PENA_ASPIRATION.ID,
            PENA_ASPIRATION.NUMERO.`as`("numero"),
            PENA_ASPIRATION.EST_NORMALISE.`as`("estNormalise"),
            PENA_ASPIRATION.EST_DEPORTE.`as`("estDeporte"),
            PENA_ASPIRATION.TYPE_PENA_ASPIRATION_ID.`as`("typePenaAspirationId"),
            PENA_ASPIRATION.HAUTEUR_SUPERIEURE_3_METRES.`as`("hauteurSuperieure3Metres"),
            DSL.field("ST_X(${PENA_ASPIRATION.GEOMETRIE})").`as`("coordonneeX"),
            DSL.field("ST_Y(${PENA_ASPIRATION.GEOMETRIE})").`as`("coordonneeY"),
        )
            .from(PENA)
            .join(PENA_ASPIRATION)
            .on(PENA_ASPIRATION.PENA_ID.eq(PENA.ID))
            .where(PENA.ID.eq(penaId))
            .fetchInto()

    fun getTypeAireAspiration(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            TYPE_PENA_ASPIRATION.ID.`as`("id"),
            TYPE_PENA_ASPIRATION.CODE.`as`("code"),
            TYPE_PENA_ASPIRATION.LIBELLE.`as`("libelle"),
        )
            .from(TYPE_PENA_ASPIRATION)
            .fetchInto()

    fun deleteAireAspiration(penaId: UUID) =
        dsl.deleteFrom(PENA_ASPIRATION)
            .where(PENA_ASPIRATION.PENA_ID.eq(penaId))
            .execute()

    fun upsertAireAspiration(penaAspiration: PenaAspiration) {
        val record = dsl.newRecord(PENA_ASPIRATION, penaAspiration)
        dsl.insertInto(PENA_ASPIRATION)
            .set(record)
            .onConflict(PENA_ASPIRATION.ID)
            .doUpdate()
            .set(record)
            .execute()
    }
}
