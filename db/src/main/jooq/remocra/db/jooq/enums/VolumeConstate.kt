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
enum class VolumeConstate(@get:JvmName("literal") public val literal: String) : EnumType {
    ZERO("ZERO"),
    UN_QUART("UN_QUART"),
    DEUX_QUARTS("DEUX_QUARTS"),
    TROIS_QUARTS("TROIS_QUARTS"),
    QUATRE_QUARTS("QUATRE_QUARTS"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "VOLUME_CONSTATE"
    override fun getLiteral(): String = literal
}
