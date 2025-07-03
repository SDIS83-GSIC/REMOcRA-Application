package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ModelePibi
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import java.util.UUID

class ModelePibiRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<ModelePibi>, AbstractRepository() {

    override fun getMapById(): Map<UUID, ModelePibi> = dsl.selectFrom(MODELE_PIBI).where(MODELE_PIBI.ACTIF.isTrue).orderBy(MODELE_PIBI.LIBELLE).fetchInto<ModelePibi>().associateBy { it.modelePibiId }

    fun getModeleWithMarque(): Collection<ModeleWithMarque> =
        dsl.select(
            MODELE_PIBI.ID.`as`("id"),
            MODELE_PIBI.CODE.`as`("code"),
            MODELE_PIBI.LIBELLE.`as`("libelle"),
            MODELE_PIBI.MARQUE_ID.`as`("marqueId"),
        )
            .from(MODELE_PIBI)
            .where(MODELE_PIBI.ACTIF)
            .fetchInto()

    data class ModeleWithMarque(
        val id: UUID,
        val code: String,
        val libelle: String,
        val marqueId: UUID,
    )
}
