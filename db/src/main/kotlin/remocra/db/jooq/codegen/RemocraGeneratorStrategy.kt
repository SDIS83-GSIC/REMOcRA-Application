package remocra.db.jooq.codegen

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.meta.ColumnDefinition
import org.jooq.meta.Definition

class RemocraGeneratorStrategy : DefaultGeneratorStrategy() {

    /**
     * Supprime le nom de table préfixant la colonne s'il existe pour son alias jOOQ
     * Exemple : table_id -> TABLE_ID -> ID
     */
    override fun getJavaIdentifier(definition: Definition?): String {
        if (definition is ColumnDefinition) {
            return super.getJavaIdentifier(definition).substringAfter("${definition.container.name}_".uppercase())
        }
        return super.getJavaIdentifier(definition)
    }
}
