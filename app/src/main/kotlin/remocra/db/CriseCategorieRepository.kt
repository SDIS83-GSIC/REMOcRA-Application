package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.CriseCategorie
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import java.util.UUID

class CriseCategorieRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<CriseCategorie>, AbstractRepository() {

    override fun getMapById(): Map<UUID, CriseCategorie> = dsl.selectFrom(CRISE_CATEGORIE).where(CRISE_CATEGORIE.ACTIF.isTrue).fetchInto<CriseCategorie>().associateBy { it.criseCategorieId }
}
