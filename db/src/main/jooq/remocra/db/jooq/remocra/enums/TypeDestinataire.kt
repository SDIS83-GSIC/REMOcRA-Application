/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.remocra.Remocra
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
enum class TypeDestinataire(@get:JvmName("literal") public val literal: String) : EnumType {
    UTILISATEUR("UTILISATEUR"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "TYPE_DESTINATAIRE"
    override fun getLiteral(): String = literal
}
