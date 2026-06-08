package remocra.db.sig.strategy

import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.db.sig.Sig
import remocra.db.sig.data.ColumnInfo
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.stream.Stream
import javax.sql.DataSource
import kotlin.streams.asStream

class DelegateQueries @Inject constructor(
    @param:Sig private val sigDatasource: Provider<DataSource>,
) {

    fun getMetaStructureTable(
        query: String,
        preparedStatement: PreparedStatement.() -> Unit,
        mapper: (rs: ResultSet) -> ColumnInfo,
    ): List<ColumnInfo> {
        return sigDatasource.get().connection.use { conn ->
            conn.prepareStatement(query).use { stmt ->
                preparedStatement.invoke(stmt)
                stmt.executeQuery().use { rs ->
                    generateSequence {
                        if (rs.next()) {
                            mapper(rs)
                        } else {
                            null
                        }
                    }.toList()
                }
            }
        }
    }

    fun selectAll(
        query: String,
    ): Stream<Map<String, Any?>> {
        val conn = sigDatasource.get().connection
        val stmt = conn.prepareStatement(query)
        val rs = stmt.executeQuery()
        val meta = rs.metaData

        return generateSequence {
            if (rs.next()) {
                (1..meta.columnCount).associate { i ->
                    meta.getColumnLabel(i).lowercase() to rs.getObject(i)
                }
            } else {
                null
            }
        }.asStream().onClose {
            rs.close()
            stmt.close()
            conn.close()
        }
    }
}

/**
 * Valide pour éviter les injections SQL
 */
fun String.validate(): String {
    require(this.isNotBlank()) { "Le paramètre '$this' ne peut pas être vide" }
    require(this.matches(Regex("^[A-Za-z0-9_\$]+$"))) {
        "Paramètre invalide: '$this' (seuls lettres, chiffres, _ et \$ sont autorisés)"
    }
    return this
}
