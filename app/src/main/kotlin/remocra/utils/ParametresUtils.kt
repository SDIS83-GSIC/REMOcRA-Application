package remocra.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.data.enums.PeiCaracteristique
import remocra.db.jooq.remocra.enums.TypeParametre
import remocra.db.jooq.remocra.tables.pojos.Parametre
import java.util.Base64

class ParametresUtils @Inject constructor() {

    companion object {
        /**
         * Méthode permettant de retourner la valeur typée d'un paramètre, en déclenchant une
         * [IllegalStateException] s'il est nul
         */
        inline fun <reified T> getParamNotNullable(
            mapParametres: Map<String, Parametre>,
            codeParametre: String,
        ): T {
            return getParam<T>(mapParametres, codeParametre)
                ?: throw IllegalStateException("Paramètre nul : $codeParametre")
        }

        /** Méthode permettant de retourner la valeur typée d'un paramètre, null y compris */
        inline fun <reified T> getParam(
            mapParametres: Map<String, Parametre>,
            codeParametre: String,
        ): T? {
            if (!mapParametres.containsKey(codeParametre)) {
                throw IllegalStateException("Paramètre manquant : $codeParametre")
            }

            val parametre = mapParametres[codeParametre]!!
            return when (parametre.parametreType) {
                TypeParametre.INTEGER -> parametre.parametreValeur?.toInt() as T
                TypeParametre.BOOLEAN -> parametre.parametreValeur?.toBoolean() as T
                TypeParametre.DOUBLE -> parametre.parametreValeur?.toDouble() as T
                // TODO !
//                TypeParametre.GEOMETRY -> parametre.parametreValeur?.toGDouble() as T
                // TODO possible ?
                TypeParametre.BINARY ->
                    if (parametre.parametreValeur == null) {
                        null
                    } else {
                        Base64.getDecoder().decode(parametre.parametreValeur) as T
                    }
                else -> parametre.parametreValeur as T
            }
        }
    }
}

/**
 * Utilitaires autour de la gestion des paramètres. Partagées entre le [ParametresProvider] qui les utilise, et le UseCase de CRUD des paramètres dans l'administration.
 */

/**
 * Retourne un [Parametre] à partir de la clé fournie en paramètre.
 * @throws IllegalArgumentException si la clé n'existe pas
 */
fun Map<String, Parametre>.getParametre(key: String): Parametre {
    val value = this[key] ?: throw IllegalArgumentException("La clé $key n'existe pas dans les paramètres")

    return value
}

/**
 * Retourne la valeur booléenne d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getBoolean(key: String): Boolean {
    val param = this.getParametre(key)
    if (TypeParametre.BOOLEAN != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur!!.toBooleanStrict()
}

/**
 * Retourne la valeur booléenne d'un paramètre dont la clé est fournie en paramètre, ou Null si le booléen n'est pas défini ou compréhensible.
 *
 * */
fun Map<String, Parametre>.getBooleanOrNull(key: String): Boolean? {
    val param = this.getParametre(key)
    if (TypeParametre.BOOLEAN != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur?.toBooleanStrictOrNull()
}

/**
 * Retourne la valeur entière (ou nulle) d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getIntOrNull(key: String): Int? {
    val param = getParametre(key)
    if (TypeParametre.INTEGER != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur?.toIntOrNull()
}

/**
 * Retourne la valeur entière d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getInt(key: String): Int {
    val param = getParametre(key)
    if (TypeParametre.INTEGER != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur!!.toInt()
}

/**
 * Retourne la valeur Double d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getDoubleOrNull(key: String): Double? {
    val param = getParametre(key)
    if (TypeParametre.DOUBLE != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur?.toDoubleOrNull()
}

/**
 * Retourne la valeur String d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getStringOrNull(key: String): String? {
    val param = getParametre(key)
    if (TypeParametre.STRING != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur
}

/**
 * Retourne la valeur String d'un paramètre dont la clé est fournie en paramètre.
 *
 * */
fun Map<String, Parametre>.getString(key: String): String {
    val param = getParametre(key)
    if (TypeParametre.STRING != param.parametreType) {
        throw IllegalArgumentException("Mauvais type demandé pour la clé $key")
    }

    return param.parametreValeur!!
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Parametre>.getListOfString(key: String, objectMapper: ObjectMapper): List<String>? {
    val paramString = getStringOrNull(key) ?: return null
    return objectMapper.readValue(paramString, List::class.java) as List<String>
}

fun Map<String, Parametre>.getListOfPeiCaracteristique(key: String, objectMapper: ObjectMapper): List<PeiCaracteristique>? {
    val paramString = getStringOrNull(key) ?: return null
    return objectMapper.readValue<List<PeiCaracteristique>>(paramString)
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Parametre>.getListOfInt(key: String, objectMapper: ObjectMapper): List<Int>? {
    val paramString = getStringOrNull(key) ?: return null
    return objectMapper.readValue(paramString, List::class.java) as List<Int>
}
