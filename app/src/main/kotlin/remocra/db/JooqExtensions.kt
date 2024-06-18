package remocra.db

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.InsertResultStep
import org.jooq.Record
import org.jooq.ResultQuery
import org.jooq.SortField
import org.jooq.TableField
import org.jooq.UpdateResultStep
import org.jooq.impl.DSL
import java.util.Optional
import java.util.UUID
import java.util.stream.Stream

// le !! ne devrait pas faire de NPE car ce fetchOneInto() est utilisé sur les returning() d'un
// insert
inline fun <reified T> InsertResultStep<*>.fetchOneInto(): T = this.fetchOne()!!.into(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchInto(): List<T> = this.fetchInto(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchStreamInto(): Stream<T> =
    this.fetchStreamInto(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchOneInto(): T? = this.fetchOneInto(T::class.java)

inline fun <reified T> UpdateResultStep<*>.fetchOneInto(): T? = this.fetchOne()?.into(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchAnyInto(): T? = this.fetchAnyInto(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchSingleInto(): T = this.fetchSingleInto(T::class.java)

inline fun <reified T> ResultQuery<*>.fetchOptionalInto(): Optional<T> =
    this.fetchOptionalInto(T::class.java)

inline fun <reified T> Record.into(): T = this.into(T::class.java)

inline fun <reified T> Field<*>.coerce(): Field<T> = this.coerce(T::class.java)

inline fun <reified T> Field<*>.cast(): Field<T> = this.cast(T::class.java)

// Critère de recherche plein texte avec le dictionnaire "fr" sur le préfixe "value"
fun Field<String?>.ftsearch(value: String): Condition {
    val escapedValued = escape(value).split(" ").filter { it != "" }.joinToString(separator = " & ")
    if (escapedValued.isBlank()) {
        return DSL.noCondition()
    }
    return DSL.condition(
        "to_tsvector('simple', {0}) @@ to_tsquery('simple', {1})",
        this,
        "$escapedValued:*",
    )
}

inline fun <R : Record, reified T> DSLContext.fetchOneById(
    idField: TableField<R, UUID>,
    id: UUID,
): T? = fetchOne(idField.table, idField.eq(id))?.into(T::class.java)

fun escape(value: String): String =
    value
        .replace("\t", " ")
        .replace("\r", "\\r")
        .replace("&", "\\&")
        .replace("@", "\\@")
        .replace("|", "\\|")
        .replace("!", "\\!")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("<", "\\<")
        // ":" est remplacé par un espace pour ne pas entrer en conflit avec ceux rajoutés
        .replace(">", "\\>")
        .replace(":", " ")
        .replace("'", "\\'")

fun Field<*>.getSortField(value: Any?): SortField<out Any>? =
    value?.let {
        when (it) {
            1 -> {
                this.asc().nullsLast()
            }
            -1 -> {
                this.desc().nullsLast()
            }
            else -> {
                null
            }
        }
    }

fun eqOrIsNullString(field: Field<String>, value: String?): Condition {
    return if (value == null) field.isNull else field.eq(value)
}

fun <T> Field<Array<T>?>.contains(value: T): Condition {
    return DSL.value(value).eq(DSL.any(this))
}
