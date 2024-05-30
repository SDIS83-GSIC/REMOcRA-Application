package remocra.utils

import jakarta.inject.Inject
import remocra.db.jooq.enums.TypeParametre
import remocra.db.jooq.tables.pojos.Parametre
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
