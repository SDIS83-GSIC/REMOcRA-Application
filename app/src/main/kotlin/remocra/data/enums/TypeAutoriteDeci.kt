package remocra.data.enums

import remocra.GlobalConstants

/**
 * Types d'organismes pouvant  alimenter la liste déroulante des "autorités de police DECI"
 */
enum class TypeAutoriteDeci(val valeurConstante: String) {
    COMMUNE(GlobalConstants.TYPE_ORGANISME_COMMUNE),
    EPCI(GlobalConstants.TYPE_ORGANISME_EPCI),
    PREFECTURE(GlobalConstants.TYPE_ORGANISME_PREFECTURE),
}
