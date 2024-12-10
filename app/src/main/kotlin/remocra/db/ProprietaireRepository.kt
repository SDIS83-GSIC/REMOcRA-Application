package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.OldebProprietaire
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETAIRE
import remocra.db.jooq.remocra.tables.references.OLDEB_PROPRIETE
import java.util.UUID

class ProprietaireRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getList(params: Params<Filter, Sort>): List<OldebProprietaire> =
        dsl.selectFrom(OLDEB_PROPRIETAIRE)
            .where(params.filterBy?.toCondition())
            .orderBy(params.sortBy?.toCondition())
            .offset(params.offset)
            .limit(params.limit)
            .fetchInto()

    fun getCount(filterBy: Filter?): Int =
        dsl.selectCount().from(OLDEB_PROPRIETAIRE)
            .where(filterBy?.toCondition())
            .fetchSingleInto()

    fun get(proprietaireId: UUID): OldebProprietaire =
        dsl.selectFrom(OLDEB_PROPRIETAIRE).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaireId)).fetchSingleInto()

    fun insertProprietaire(proprietaire: OldebProprietaire): Int =
        dsl.insertInto(OLDEB_PROPRIETAIRE).set(dsl.newRecord(OLDEB_PROPRIETAIRE, proprietaire)).execute()

    fun updateProprietaire(proprietaire: OldebProprietaire): Int =
        dsl.update(OLDEB_PROPRIETAIRE).set(dsl.newRecord(OLDEB_PROPRIETAIRE, proprietaire)).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaire.oldebProprietaireId)).execute()

    fun deleteProprietaire(proprietaireId: UUID): Int =
        dsl.deleteFrom(OLDEB_PROPRIETAIRE).where(OLDEB_PROPRIETAIRE.ID.eq(proprietaireId)).execute()

    fun isProprietaireInUse(proprietaireId: UUID): Boolean =
        dsl.fetchExists(
            dsl.select(OLDEB_PROPRIETE.OLDEB_PROPRIETAIRE_ID).from(OLDEB_PROPRIETE).where(OLDEB_PROPRIETE.OLDEB_PROPRIETAIRE_ID.eq(proprietaireId)),
        )

    data class Filter(
        val oldebProprietaireNom: String?,
        val oldebProprietairePrenom: String?,
        val oldebProprietaireVille: String?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    oldebProprietaireNom?.let { OLDEB_PROPRIETAIRE.NOM.likeIgnoreCase("%$it%") },
                    oldebProprietairePrenom?.let { OLDEB_PROPRIETAIRE.PRENOM.likeIgnoreCase("%$it%") },
                    oldebProprietaireVille?.let { OLDEB_PROPRIETAIRE.VILLE.likeIgnoreCase("%$it%") },
                ),
            )
    }

    data class Sort(
        val oldebProprietaireNom: Int?,
        val oldebProprietairePrenom: Int?,
        val oldebProprietaireVille: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            OLDEB_PROPRIETAIRE.NOM.getSortField(oldebProprietaireNom),
            OLDEB_PROPRIETAIRE.PRENOM.getSortField(oldebProprietairePrenom),
            OLDEB_PROPRIETAIRE.VILLE.getSortField(oldebProprietaireVille),
        )
    }
}
