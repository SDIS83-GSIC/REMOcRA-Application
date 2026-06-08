package remocra.db.sig.data

data class ColumnInfo(

    val schemaName: String,
    val columnName: String,
    val columnType: SigPostgreSQLType,
    val columnNullable: Boolean,
)

fun List<ColumnInfo>.joinColumnNames(): String = this.joinToString(", ") { it.columnName }
