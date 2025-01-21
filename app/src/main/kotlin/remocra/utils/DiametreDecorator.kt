package remocra.utils

import remocra.GlobalConstants

class DiametreDecorator {

    fun decorateDiametre(diametreCode: String): Int? {
        return when (diametreCode) {
            GlobalConstants.DIAMETRE_70 -> 70
            GlobalConstants.DIAMETRE_80 -> 80
            GlobalConstants.DIAMETRE_100 -> 100
            GlobalConstants.DIAMETRE_150 -> 150
            else -> null
        }
    }
}
