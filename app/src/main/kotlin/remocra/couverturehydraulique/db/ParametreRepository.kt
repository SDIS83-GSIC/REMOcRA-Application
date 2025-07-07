package remocra.couverturehydraulique.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.enums.CodeSdis
import remocra.db.AbstractRepository
import java.util.UUID

/**
 * Repository pour les paramètres de configuration
 */
class ParametreRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Vérifie si une fonction spécifique SDIS existe
     */
    fun existsFonctionSpecifiqueSdis(codeSdis: CodeSdis): Boolean {
        val nomFonction = "couverture_hydraulique_zonage_${codeSdis.name}"
        return dsl.selectCount()
            .from("pg_proc")
            .where(DSL.field("proname").eq(nomFonction))
            .fetchOne(0, Int::class.java)?.let { it > 0 } ?: false
    }

    /**
     * Exécute une fonction spécifique SDIS
     */
    fun executeFonctionSpecifiqueSdis(codeSdis: CodeSdis, idEtude: UUID) {
        val nomFonction = "couverture_hydraulique_zonage_${codeSdis.name}"
        dsl.execute("SELECT * FROM couverture_hydraulique.$nomFonction(?)", idEtude)
    }
}
