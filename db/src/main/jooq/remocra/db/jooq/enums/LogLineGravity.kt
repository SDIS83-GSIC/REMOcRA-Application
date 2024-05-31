/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.Remocra
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
enum class LogLineGravity(@get:JvmName("literal") public val literal: String) : EnumType {
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "log_line_gravity"
    override fun getLiteral(): String = literal
}
