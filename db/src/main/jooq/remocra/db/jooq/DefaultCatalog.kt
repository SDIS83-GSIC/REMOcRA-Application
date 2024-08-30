/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq

import org.jooq.Constants
import org.jooq.Schema
import org.jooq.impl.CatalogImpl
import remocra.db.jooq.couverturehydraulique.Couverturehydraulique
import remocra.db.jooq.historique.Historique
import remocra.db.jooq.remocra.Remocra
import javax.annotation.processing.Generated
import kotlin.collections.List

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
open class DefaultCatalog : CatalogImpl("") {
    companion object {

        /**
         * The reference instance of <code>DEFAULT_CATALOG</code>
         */
        public val DEFAULT_CATALOG: DefaultCatalog = DefaultCatalog()
    }

    /**
     * The schema <code>couverturehydraulique</code>.
     */
    val COUVERTUREHYDRAULIQUE: Couverturehydraulique get(): Couverturehydraulique = Couverturehydraulique.COUVERTUREHYDRAULIQUE

    /**
     * The schema <code>historique</code>.
     */
    val HISTORIQUE: Historique get(): Historique = Historique.HISTORIQUE

    /**
     * The schema <code>remocra</code>.
     */
    val REMOCRA: Remocra get(): Remocra = Remocra.REMOCRA

    override fun getSchemas(): List<Schema> = listOf(
        Couverturehydraulique.COUVERTUREHYDRAULIQUE,
        Historique.HISTORIQUE,
        Remocra.REMOCRA,
    )

    /**
     * A reference to the 3.19 minor release of the code generator. If this
     * doesn't compile, it's because the runtime library uses an older minor
     * release, namely: 3.19. You can turn off the generation of this reference
     * by specifying /configuration/generator/generate/jooqVersionReference
     */
    private val REQUIRE_RUNTIME_JOOQ_VERSION = Constants.VERSION_3_19
}
