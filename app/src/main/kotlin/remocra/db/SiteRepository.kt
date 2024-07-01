package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.SITE
import java.util.UUID

class SiteRepository @Inject constructor(private val dsl: DSLContext) {

    fun getAll(): Collection<SiteWithGestionnaire> =
        dsl.select(SITE.ID.`as`("id"), SITE.LIBELLE.`as`("libelle"), GESTIONNAIRE.ID)
            .from(SITE)
            .join(GESTIONNAIRE)
            .on(GESTIONNAIRE.ID.eq(SITE.GESTIONNAIRE_ID))
            .where(SITE.ACTIF)
            .and(GESTIONNAIRE.ACTIF)
            .fetchInto()

    data class SiteWithGestionnaire(
        val id: UUID,
        val libelle: String,
        val gestionnaireId: UUID,
    )
}
