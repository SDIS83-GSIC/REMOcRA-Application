package remocra.db.sig.strategy

import remocra.db.sig.data.ColumnInfo
import java.util.stream.Stream

class NotConfiguredSigQueries : SigQueries {
    override fun getMetaStructureTable(
        schemaName: String,
        tableName: String,
    ): List<ColumnInfo> {
        throw IllegalStateException(
            "Le module SIG n'est configuré dans REMOcRA. " +
                "Veuillez vérifier la configuration de votre application.",
        )
    }

    override fun selectAll(
        listFields: List<ColumnInfo>,
        schemaSource: String,
        tableSource: String,
    ): Stream<Map<String, Any?>> {
        throw IllegalStateException(
            "Le module SIG n'est configuré dans REMOcRA. " +
                "Veuillez vérifier la configuration de votre application.",
        )
    }
}
