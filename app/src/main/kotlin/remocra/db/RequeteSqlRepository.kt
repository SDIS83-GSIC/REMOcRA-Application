package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.utils.BuildDynamicForm

class RequeteSqlRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun executeSqlParametre(requete: String): List<BuildDynamicForm.IdLibelleDynamicForm> =
        dsl.fetch(requete).into(BuildDynamicForm.IdLibelleDynamicForm::class.java)
}
