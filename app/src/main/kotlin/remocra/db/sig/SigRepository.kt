package remocra.db.sig

import jakarta.inject.Inject
import remocra.db.sig.data.ColumnInfo
import remocra.db.sig.strategy.SigQueries
import kotlin.streams.asSequence

class SigRepository @Inject constructor(
    private val sigQueries: SigQueries,
) {

    fun getMetaStructureTable(schemaName: String, tableName: String): List<ColumnInfo> {
        return sigQueries
            .getMetaStructureTable(schemaName, tableName)
    }

    /**
     * Traitement par BATCH (streaming) pour les très grandes tables
     * Évite de charger toutes les données en RAM
     *
     * @param listFields : champs à sélectionner
     * @param schemaSource : schéma source
     * @param tableSource : table source
     * @param batchSize : nombre de lignes par batch (défaut 50 000)
     * @param processBatch : fonction appelée pour chaque batch
     */
    fun selectAllByBatch(
        listFields: List<ColumnInfo>,
        schemaSource: String,
        tableSource: String,
        batchSize: Int,
        processBatch: (List<Map<String, Any?>>) -> Unit,
    ) {
        sigQueries
            .selectAll(listFields, schemaSource, tableSource)
            .asSequence()
            .chunked(batchSize)
            .forEach(processBatch)
    }
}
