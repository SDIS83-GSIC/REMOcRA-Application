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

/**
 * Fait appel à la primitive Postgres *unaccent* sur *this*
 */
fun Field<String?>.unaccent() = DSL.field("unaccent({0})", this.dataType, this)

/**
 * Fait appel à la primitive Postgres *unaccent* sur *value*
 */
fun unaccent(value: String) = DSL.field("unaccent({0})", String::class.java, value)

/**
 * Effectue une recherche d'égalité textuelle désaccentuée en wrappant *this* et *value* dans un *unaccent* postgres
 */
fun Field<String?>.equalUnaccent(value: String): Condition = this.unaccent().eq(unaccent(value))

/**
 * Effectue une recherche d'égalité textuelle désaccentuée et insensible à la casse en wrappant *this* et *value* dans un *unaccent* et un *lower* postgres
 */
fun Field<String?>.equalIgnoreCaseUnaccent(value: String): Condition = this.unaccent().equalIgnoreCase(unaccent(value))

/**
 * Effectue un CONTAINS désaccentué en wrappant *this* et *value* dans un *unaccent*
 *
 */
fun Field<String?>.containsUnaccent(value: String): Condition = this.unaccent().contains(unaccent(value))

/**
 * Effectue un CONTAINS désaccentué et insensible à la casse en wrappant *this* et *value* dans un *unaccent* et un *lower* postgres
 */
fun Field<String?>.containsIgnoreCaseUnaccent(value: String): Condition = this.unaccent().containsIgnoreCase(unaccent(value))

/**
 * Effectue un like 'value%' désaccentué  en wrappant *this* et *value* dans un *unaccent*
 *
 */
fun Field<String?>.startsWithUnaccent(value: String): Condition = this.unaccent().startsWith(unaccent(value))

/**
 * Effectue un like 'value%' désaccentué insensible à la casse en wrappant *this* et *value* dans un *unaccent*
 *
 */
fun Field<String?>.startsWithIgnoreCaseUnaccent(value: String): Condition = this.unaccent().startsWithIgnoreCase(unaccent(value))

/**
 * Effectue un like '%value' désaccentué  en wrappant *this* et *value* dans un *unaccent*
 *
 */
fun Field<String?>.endsWithUnaccent(value: String): Condition = this.unaccent().endsWith(unaccent(value))

/**
 * Effectue un like '%value' désaccentué insensible à la casse en wrappant *this* et *value* dans un *unaccent*
 *
 */
fun Field<String?>.endsWithIgnoreCaseUnaccent(value: String): Condition = this.unaccent().endsWithIgnoreCase(unaccent(value))

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

/**
 * Cette méthode génère une condition jOOQ basée sur une valeur booléenne fournie.
 *
 * @param condition La valeur booléenne utilisée pour déterminer la condition de la requête.
 *                  Si `true`, la condition vérifiera que le champ est vrai.
 *                  Si `false`, la condition vérifiera que le champ est faux.
 * @param field     Le champ de la table (de type `TableField`) à vérifier.
 *                  Ce champ doit être de type `Boolean?` pour permettre des valeurs nulles.
 *
 * @return Une condition jOOQ (`Condition`) qui vérifie si le champ est `true` ou `false`
 *         selon la valeur du paramètre `condition`.
 */
fun booleanFilter(condition: Boolean, field: TableField<Record, Boolean?>): Condition {
    return if (condition) field.isTrue() else field.isFalse()
}
