package remocra.db.rawsql

import org.jooq.SQL
import org.jooq.impl.DSL
import java.util.UUID

class RawSqlQueryBuilder private constructor(
    private val query: String,
) {
    companion object {
        fun String.toRawSqlQueryBuilder() = RawSqlQueryBuilder(this)
    }

    private val substitutions: MutableList<Substitution> = mutableListOf()

    /**
     * Rajoute une substitution de paramètre dans la requête SQL.
     * Le token sera remplacé par un "?" et la valeur sera ajoutée à la liste des paramètres.
     * @param pair un Pair contenant le token à remplacer et la valeur à utiliser pour le bind parameter.
     */
    infix fun withBindParam(pair: Pair<String, Any>) {
        substitutions += Substitution.BindParam(pair.first, pair.second)
    }

    infix fun withRawReplace(pair: Pair<Regex, String>) {
        substitutions += Substitution.RawReplace(pair.first, pair.second)
    }

    fun build(): SQL {
        // 1) Appliquer d'abord les RawReplace (ils modifient la structure SQL)
        var builtQuery = query
        for (sub in substitutions.filterIsInstance<Substitution.RawReplace>()) {
            builtQuery = builtQuery.replace(sub.token, sub.value)
        }

        // 2) Pour les BindParam, trouver toutes les occurrences avec leur position
        val bindSubs = substitutions.filterIsInstance<Substitution.BindParam>()

        val hits = bindSubs.flatMap { sub ->
            /*
            Pour compatibilité avec les requêtes SQL déjà écrites en base, on gère les deux cas :
            le token peut être entouré de single quotes ('token') ou non (token). Dans les deux cas
            on remplace la totalité (quotes incluses) par un unique "?" qui est un bind marker valide,
            car un "?" à l'intérieur d'un littéral SQL n'est pas reconnu comme bind marker par JDBC/jOOQ.
            L'alternance place la forme quotée en premier pour qu'elle soit consommée en entier.
             */
            val escaped = Regex.escape(sub.token)
            Regex("'$escaped'|$escaped")
                .findAll(builtQuery)
                .map {
                    Hit(it.range.first, it.range.last + 1, sub.value)
                }
        }.sortedBy { it.start }

        // 3) Reconstruire la query en remplaçant chaque hit par "?" et collecter les params dans l'ordre
        val sb = StringBuilder()
        val params = mutableListOf<Any>()
        var cursor = 0
        for (hit in hits) {
            sb.append(builtQuery, cursor, hit.start).append("?")
            params += hit.value.tryConvert()
            cursor = hit.end
        }
        sb.append(builtQuery, cursor, builtQuery.length)

        return DSL.sql(sb.toString(), *params.toTypedArray())
    }

    /**
     * On essaie de convertir la valeur en Int ou UUID si c'est une String, car sinon les bind parame
     * tres sont toujours considérés comme des String et ça peut poser problème pour certaines requêtes.
     */
    private fun Any.tryConvert(): Any =
        when (this) {
            is String -> runCatching { this.toInt() }
                .fold(
                    onSuccess = { it },
                    onFailure = {
                        runCatching { UUID.fromString(this) }
                            .fold(
                                onSuccess = { it },
                                onFailure = { this },
                            )
                    },
                )
            else -> this
        }

    private sealed interface Substitution {
        data class BindParam(val token: String, val value: Any) : Substitution
        data class RawReplace(val token: Regex, val value: String) : Substitution
    }
    private data class Hit(val start: Int, val end: Int, val value: Any)
}
