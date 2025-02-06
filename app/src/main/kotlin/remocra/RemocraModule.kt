package remocra

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import dev.misfitlabs.kotlinguice4.KotlinModule

/**
 * Classe de base pour tous les modules REMOcRA ; pour l'instant, elle est vide, mais elle donne au moins accès à la fonction d'extension [getStringOrNull]
 */
abstract class RemocraModule : KotlinModule()

/**
 * Retourne la valeur de config définie, ou NULL si absente. A n'utiliser que sur une *property* nullable, pour éviter une exception.
 */
fun Config.getStringOrNull(path: String): String? {
    return try {
        this.getString(path)
    } catch (missing: ConfigException.Missing) {
        null
    }
}

/**
 * Retourne la valeur de config définie, ou NULL si absente. A n'utiliser que sur une *property* nullable, pour éviter une exception.
 */
fun Config.getBytesOrNull(path: String): Long? {
    return try {
        this.getBytes(path)
    } catch (missing: ConfigException.Missing) {
        null
    }
}
