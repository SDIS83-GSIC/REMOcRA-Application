/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.couverturehydraulique.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.couverturehydraulique.Couverturehydraulique
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
enum class EtudeStatut(@get:JvmName("literal") public val literal: String) : EnumType {
    EN_COURS("EN_COURS"),
    TERMINEE("TERMINEE"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Couverturehydraulique.COUVERTUREHYDRAULIQUE
    override fun getName(): String = "etude_statut"
    override fun getLiteral(): String = literal
}
