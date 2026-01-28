package remocra.data.enums

import remocra.GlobalConstants

/**
 * Types d'organismes pouvant alimenter la liste déroulante des "services publics DECI"
 */
enum class TypeServicePublicDeci(val valeurConstante: String) {
    COMMUNE(GlobalConstants.TYPE_ORGANISME_COMMUNE),
    EPCI(GlobalConstants.TYPE_ORGANISME_EPCI),
    AUTRE_SERVICE_PUBLIC_DECI(GlobalConstants.TYPE_ORGANISME_AUTRE_SERVICE_PUBLIC_DECI),
}
