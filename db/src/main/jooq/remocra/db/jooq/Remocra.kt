/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl
import remocra.db.jooq.tables.Utilisateur
import javax.annotation.processing.Generated
import kotlin.collections.List

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
open class Remocra : SchemaImpl("remocra", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>remocra</code>
         */
        val REMOCRA: Remocra = Remocra()
    }

    /**
     * The table <code>remocra.utilisateur</code>.
     */
    val UTILISATEUR: Utilisateur get() = Utilisateur.UTILISATEUR

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        Utilisateur.UTILISATEUR,
    )
}
