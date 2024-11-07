/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import java.io.Serializable
import java.util.UUID
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class DashboardQuery(
    val dashboardQueryId: UUID,
    val dashboardQueryTitle: String,
    val dashboardQueryQuery: String,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        val o: DashboardQuery = other as DashboardQuery
        if (this.dashboardQueryId != o.dashboardQueryId) {
            return false
        }
        if (this.dashboardQueryTitle != o.dashboardQueryTitle) {
            return false
        }
        if (this.dashboardQueryQuery != o.dashboardQueryQuery) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.dashboardQueryId.hashCode()
        result = prime * result + this.dashboardQueryTitle.hashCode()
        result = prime * result + this.dashboardQueryQuery.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("DashboardQuery (")

        sb.append(dashboardQueryId)
        sb.append(", ").append(dashboardQueryTitle)
        sb.append(", ").append(dashboardQueryQuery)

        sb.append(")")
        return sb.toString()
    }
}
