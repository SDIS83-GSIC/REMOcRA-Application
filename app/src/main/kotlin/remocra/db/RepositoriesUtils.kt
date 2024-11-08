package remocra.db

import org.jooq.Condition
import org.jooq.impl.DSL

class RepositoriesUtils {

    fun checkIsSuperAdminOrCondition(condition: Condition, isSuperAdmin: Boolean) =
        if (isSuperAdmin) {
            DSL.noCondition()
        } else {
            condition
        }
}
