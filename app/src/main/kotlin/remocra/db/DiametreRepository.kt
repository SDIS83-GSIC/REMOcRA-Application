package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Diametre
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.L_DIAMETRE_NATURE
import java.util.UUID

class DiametreRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<Diametre> {

    override fun getMapById(): Map<UUID, Diametre> = dsl.selectFrom(DIAMETRE).where(DIAMETRE.ACTIF.isTrue).fetchInto<Diametre>().associateBy { it.diametreId }

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
}
