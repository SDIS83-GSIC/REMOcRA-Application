package remocra.web.admin

import remocra.db.jooq.remocra.enums.TypeParametre
import remocra.db.jooq.remocra.tables.pojos.Parametre

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
