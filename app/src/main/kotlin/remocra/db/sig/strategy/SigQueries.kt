package remocra.db.sig.strategy

import remocra.db.sig.data.ColumnInfo
import java.util.stream.Stream

/**
 * Interface de la strategy qui permet d'exécuter les requêtes sans se soucier du fournisseur de la base de données.
 */
interface SigQueries {

    fun getMetaStructureTable(schemaName: String, tableName: String): List<ColumnInfo>

    fun selectAll(
        listFields: List<ColumnInfo>,
        schemaSource: String,
        tableSource: String,
    ): Stream<Map<String, Any?>>
}
