package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.L_DIAMETRE_NATURE
import java.util.UUID

class DiametreRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Diametre> {

    override fun getMapById(): Map<UUID, Diametre> =
        dsl.selectFrom(DIAMETRE).where(DIAMETRE.ACTIF.isTrue).fetchInto<Diametre>().associateBy { it.diametreId }

    fun getDiametreWithIdNature(): Collection<DiametreWithNature> =
        dsl.select(
            DIAMETRE.ID.`as`("id"),
            DIAMETRE.CODE.`as`("code"),
            DIAMETRE.LIBELLE.`as`("libelle"),
            L_DIAMETRE_NATURE.NATURE_ID,
        ).from(DIAMETRE)
            .join(L_DIAMETRE_NATURE)
            .on(L_DIAMETRE_NATURE.DIAMETRE_ID.eq(DIAMETRE.ID))
            .fetchInto()

    data class DiametreWithNature(
        val id: UUID,
        val code: String,
        val libelle: String,
        val natureId: UUID,
    )

    data class Filter(
        val diametreCode: String?,
        val diametreLibelle: String?,
        val diametreActif: Boolean?,
        val diametreProtected: Boolean?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    diametreCode?.let { DSL.and(DIAMETRE.CODE.contains(diametreCode)) },
                    diametreLibelle?.let { DSL.and(DIAMETRE.LIBELLE.contains(diametreLibelle)) },
                    diametreActif?.let { DSL.and(DIAMETRE.ACTIF.eq(diametreActif)) },
                    diametreProtected?.let { DSL.and(DIAMETRE.PROTECTED.eq(diametreProtected)) },
                ),
            )
    }

    data class Sort(
        val diametreCode: Int?,
        val diametreLibelle: Int?,
        val diametreActif: Int?,
        val diametreProtected: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            DIAMETRE.CODE.getSortField(diametreCode),
            DIAMETRE.LIBELLE.getSortField(diametreLibelle),
            DIAMETRE.ACTIF.getSortField(diametreActif),
            DIAMETRE.PROTECTED.getSortField(diametreProtected),
        )
    }

    fun remove(id: UUID) =
        dsl.deleteFrom(DIAMETRE).where(DIAMETRE.ID.eq(id)).and(DIAMETRE.PROTECTED.isFalse).execute()

    fun add(diametre: Diametre) =
        dsl.insertInto(DIAMETRE).set(
            dsl.newRecord(DIAMETRE, diametre),
        ).execute()

    fun edit(id: UUID, code: String, libelle: String, active: Boolean): Int {
        return dsl.update(DIAMETRE).set(DIAMETRE.CODE, code).set(DIAMETRE.LIBELLE, libelle).set(DIAMETRE.ACTIF, active)
            .where(DIAMETRE.ID.eq(id)).and(DIAMETRE.PROTECTED.isFalse).execute()
    }

    fun get(params: Params<Filter, Sort>): Collection<Diametre> {
        return dsl.select(DIAMETRE.fields().asList()).from(DIAMETRE)
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition()).orderBy(
                params.sortBy?.toCondition() ?: listOf(
                    DIAMETRE.CODE,
                ),
            ).limit(params.limit).offset(params.offset).fetchInto()
    }

    fun getCount(params: Params<Filter, Sort>): Int {
        return dsl.selectCount().from(DIAMETRE).where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()
    }

    fun getById(id: UUID): Diametre? {
        return dsl.select(DIAMETRE.fields().asList()).from(DIAMETRE).where(DIAMETRE.ID.eq(id)).fetchOneInto()
    }
}
