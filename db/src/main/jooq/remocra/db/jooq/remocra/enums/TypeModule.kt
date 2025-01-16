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
enum class TypeModule(@get:JvmName("literal") public val literal: String) : EnumType {
    DECI("DECI"),
    COUVERTURE_HYDRAULIQUE("COUVERTURE_HYDRAULIQUE"),
    CARTOGRAPHIE("CARTOGRAPHIE"),
    OLDEBS("OLDEBS"),
    PERMIS("PERMIS"),
    RCI("RCI"),
    DFCI("DFCI"),
    ADRESSES("ADRESSES"),
    RISQUES("RISQUES"),
    ADMIN("ADMIN"),
    COURRIER("COURRIER"),
    DOCUMENT("DOCUMENT"),
    PERSONNALISE("PERSONNALISE"),
    RAPPORT_PERSONNALISE("RAPPORT_PERSONNALISE"),
    DASHBOARD("DASHBOARD"),
    OPERATIONS_DIVERSES("OPERATIONS_DIVERSES"),
    CRISE("CRISE"),
    PEI_PRESCRIT("PEI_PRESCRIT"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "type_module"
    override fun getLiteral(): String = literal
}
