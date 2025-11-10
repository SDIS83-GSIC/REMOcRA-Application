package remocra.utils

/**
 * Présente une géométrie parsée qui contient SRID et une WKT geometry (tous deux format STRING)
 *
 * @property srid L'identifiant du système de référence spatiale extrait de la chaîne d'entrée.
 * @property wkt  La géométrie du texte (WKT) extraite de la chaîne.
 */
data class ParsedGeom(val srid: String, val wkt: String)

/**
 * Analyse une chaîne de paramètres géométriques de la forme `"srid=XXXX;WKT_VALUE"`
 *
 * @param value value La chaîne de paramètres brute contenant à la fois le SRID et la géométrie WKT.
 *
 * @return Une instance de [ParsedGeom] contenant :
 * - srid : le SRID numérique extrait après `"srid="`
 * - wkt : la chaîne de géométrie WKT suivant le point-virgule
 *
 * @throws IllegalArgumentException si l'entrée ne contient pas les deux parties.
 */
fun parseParam(value: String): ParsedGeom {
    val (sridPart, wkt) = value.split(";", limit = 2)
    return ParsedGeom(
        srid = sridPart.substringAfter("="),
        wkt = wkt,
    )
}

/**
 * Vérifie si la valeur du paramètre contient une expression géométrique
 * au format : "srid=<SRID_VALUE>;<WKT_GEOMETRY>".
 *
 * @param value la chaîne de caractères brute du paramètre
 * @return true si la chaîne peut être analysée par [parseParam]
 */
fun canParseParam(value: String?): Boolean {
    return value
        ?.let { ";" in it && it.startsWith("srid=", ignoreCase = true) }
        ?: false
}
