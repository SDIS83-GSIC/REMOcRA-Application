package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.L_TOPONYMIE_CRISE
import remocra.db.jooq.remocra.tables.references.TYPE_TOPONYMIE
import java.util.UUID

class ToponymieRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getToponymieForSelect(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(TYPE_TOPONYMIE.ID.`as`("id"), TYPE_TOPONYMIE.CODE.`as`("code"), TYPE_TOPONYMIE.LIBELLE.`as`("libelle"))
            .from(TYPE_TOPONYMIE)
            .orderBy(TYPE_TOPONYMIE.LIBELLE)
            .fetchInto()

    fun insertLToponymieCrise(listeToponymieId: Collection<UUID>?, criseId: UUID) {
        dsl.batch(
            listeToponymieId?.map {
                DSL.insertInto(L_TOPONYMIE_CRISE)
                    .set(L_TOPONYMIE_CRISE.TYPE_TOPONYMIE_ID, it)
                    .set(L_TOPONYMIE_CRISE.CRISE_ID, criseId)
            },
        ).execute()
    }
}
