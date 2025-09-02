package remocra.data.enums

/**
 * Cette énumération définit les différentes caractéristiques d'un PEI. Bien faire attention à
 * utiliser des codes UNIQUE
 */
enum class PeiCaracteristique(val libelle: String, val typeCaracterique: TypeCaracterique) {
    // Général
    NUMERO_COMPLET("Numéro complet", TypeCaracterique.GENERAL),
    TYPE_PEI("Type du PEI", TypeCaracterique.GENERAL),
    NATURE_PEI("Nature du PEI", TypeCaracterique.GENERAL),
    AUTORITE_POLICE("Autorité de police DECI", TypeCaracterique.GENERAL),
    TYPE_DECI("Type de DECI", TypeCaracterique.GENERAL),
    SERVICE_PUBLIC("Service public DECI", TypeCaracterique.GENERAL),
    MAINTENANCE_CTP("Maintenance et CTP", TypeCaracterique.GENERAL), // Localisation
    COMPLEMENT("Complément d'adresse", TypeCaracterique.GENERAL),
    DATE_RECEPTION("Date de réception", TypeCaracterique.GENERAL),

    // PIBI
    DIAMETRE_NOMINAL("Diamètre nominal", TypeCaracterique.PIBI),
    DEBIT("Débit", TypeCaracterique.PIBI),
    JUMELE("Jumelé", TypeCaracterique.PIBI),
    GROS_DEBIT("Gros débit", TypeCaracterique.PIBI),

    // PENA
    CAPACITE("Capacité", TypeCaracterique.PENA),

    ;

    companion object {
        /**
         * Convertit une chaîne de caractères en une valeur de l'énumération PeiCaracteristique.
         *
         * @param stringValue La chaîne de caractères à convertir.
         * @return La valeur correspondante de l'énumération.
         * @throws IllegalArgumentException Si la valeur n'a pas été trouvée.
         */
        fun fromString(stringValue: String): PeiCaracteristique {
            return entries.find { it.libelle == stringValue }
                ?: throw IllegalArgumentException("PeiCaracteristique : valeur '$stringValue' non trouvée")
        }
    }
}

enum class TypeCaracterique {
    GENERAL,
    PENA,
    PIBI,
}
