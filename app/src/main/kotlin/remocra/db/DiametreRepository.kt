package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.data.ApiDiametreCodeLibelle
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.L_DIAMETRE_NATURE
import remocra.db.jooq.remocra.tables.references.NATURE
import java.util.UUID

class DiametreRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Diametre>, AbstractRepository() {

    override fun getMapById(): Map<UUID, Diametre> =
        dsl.selectFrom(DIAMETRE).where(DIAMETRE.ACTIF.isTrue).orderBy(DIAMETRE.LIBELLE).fetchInto<Diametre>().associateBy { it.diametreId }

    fun getDiametreWithIdNature(): Collection<DiametreWithNature> =
        dsl.select(
            DIAMETRE.ID.`as`("id"),
            DIAMETRE.CODE.`as`("code"),
            DIAMETRE.LIBELLE.`as`("libelle"),
            DIAMETRE.ACTIF.`as`("actif"),
            L_DIAMETRE_NATURE.NATURE_ID,
        ).from(DIAMETRE)
            .join(L_DIAMETRE_NATURE)
            .on(L_DIAMETRE_NATURE.DIAMETRE_ID.eq(DIAMETRE.ID))
            .fetchInto()

    data class DiametreWithNature(
        val id: UUID,
        val code: String,
        val libelle: String,
        val actif: Boolean,
        val natureId: UUID,
    )

    fun getDiametres(natureCode: String, limit: Long?, offset: Long?): Collection<ApiDiametreCodeLibelle> =
        dsl.select(
            DIAMETRE.CODE,
            DIAMETRE.LIBELLE,
        ).from(DIAMETRE)
            .join(L_DIAMETRE_NATURE)
            .on(L_DIAMETRE_NATURE.DIAMETRE_ID.eq(DIAMETRE.ID))
            .join(NATURE)
            .on(NATURE.ID.eq(L_DIAMETRE_NATURE.NATURE_ID))
            .where(NATURE.CODE.equalIgnoreCase(natureCode))
            .limit(limit)
            .offset(offset)
            .fetchInto()
}
