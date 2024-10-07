package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.ProfilOrganisme
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME

class ProfilOrganismeRepository @Inject constructor(private val dsl: DSLContext) {
    fun getActive(): Collection<ProfilOrganisme> {
        return dsl.select(PROFIL_ORGANISME.fields().toList()).from(PROFIL_ORGANISME)
            .where(PROFIL_ORGANISME.ACTIF.isTrue).orderBy(PROFIL_ORGANISME.LIBELLE).fetchInto()
    }
}
