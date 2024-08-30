/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.historique.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.historique.Historique
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
enum class TypeObjet(@get:JvmName("literal") public val literal: String) : EnumType {
    PEI("PEI"),
    VISITE("VISITE"),
    PARAMETRE("PARAMETRE"),
    GESTIONNAIRE("GESTIONNAIRE"),
    SITE("SITE"),
    PENA_ASPIRATION("PENA_ASPIRATION"),
    DOCUMENT_PEI("DOCUMENT_PEI"),
    ETUDE("ETUDE"),
    DOCUMENT_ETUDE("DOCUMENT_ETUDE"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Historique.HISTORIQUE
    override fun getName(): String = "type_objet"
    override fun getLiteral(): String = literal
}
