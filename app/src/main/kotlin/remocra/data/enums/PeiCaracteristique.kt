package remocra.data.enums

/**
 * Cette énumération définit les différentes caractéristiques d'un PEI. Bien faire attention à
 * utiliser des codes UNIQUE
 */
enum class PeiCaracteristique(val libelle: String, val typeCaracteristique: TypeCaracteristique) {
    // Général
    NUMERO_COMPLET("Numéro complet", TypeCaracteristique.GENERAL),
    TYPE_PEI("Type du PEI", TypeCaracteristique.GENERAL),
    NATURE_PEI("Nature du PEI", TypeCaracteristique.GENERAL),
    AUTORITE_POLICE("Autorité de police DECI", TypeCaracteristique.GENERAL),
    TYPE_DECI("Type de DECI", TypeCaracteristique.GENERAL),
    SERVICE_PUBLIC("Service public DECI", TypeCaracteristique.GENERAL),
    MAINTENANCE_CTP("Maintenance et CTP", TypeCaracteristique.GENERAL), // Localisation
    COMPLEMENT("Complément d'adresse", TypeCaracteristique.GENERAL),
    DATE_RECEPTION("Date de réception", TypeCaracteristique.GENERAL),
    ADRESSE("Adresse", TypeCaracteristique.GENERAL),

    // PIBI
    DIAMETRE_NOMINAL("Diamètre nominal", TypeCaracteristique.PIBI),
    DEBIT("Débit", TypeCaracteristique.PIBI),
    JUMELE("Jumelé", TypeCaracteristique.PIBI),
    GROS_DEBIT("Gros débit", TypeCaracteristique.PIBI),

    // PENA
    CAPACITE("Capacité", TypeCaracteristique.PENA),

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

enum class TypeCaracteristique {
    GENERAL,
    PENA,
    PIBI,
}
