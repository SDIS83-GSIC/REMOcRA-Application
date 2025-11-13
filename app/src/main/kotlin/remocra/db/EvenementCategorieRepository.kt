package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.EvenementCategorie
import remocra.db.jooq.remocra.tables.references.EVENEMENT_CATEGORIE
import java.util.UUID

class EvenementCategorieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<EvenementCategorie>, AbstractRepository() {

    override fun getMapById(): Map<UUID, EvenementCategorie> = dsl.selectFrom(EVENEMENT_CATEGORIE).where(EVENEMENT_CATEGORIE.ACTIF.isTrue).orderBy(
        EVENEMENT_CATEGORIE.LIBELLE,
    ).fetchInto<EvenementCategorie>().associateBy { it.evenementCategorieId }
}
