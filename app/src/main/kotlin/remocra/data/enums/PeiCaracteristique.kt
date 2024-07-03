package remocra.data.enums

import java.util.Arrays

/**
 * Cette énumération définit les différentes caractéristiques d'un PEI. Bien faire attention à
 * utiliser des codes UNIQUE
 */
enum class PeiCaracteristique(val code: String, val libelle: String) {
    // Général
    TYPE_PEI("typePei", "Type du PEI"),
    NATURE_PEI("naturePei", "Nature du PEI"),
    AUTORITE_POLICE("autoritePolice", "Autorité de police DECI"),
    TYPE_DECI("typeDeci", "Type de DECI"),
    SERVICE_PUBLIC("servicePublic", "Service public DECI"),
    MAINTENANCE_CTP("maintenanceCtp", "Maintenance et CTP"), // Localisation

    // Caractéristiques techniques
    DIAMETRE_NOMINAL("diametreNominal", "Diamètre nominal"),
    DEBIT("debit", "Débit"),
    CAPACITE("capacite", "Capacité"),

    COMPLEMENT("complement", "Complément d'adresse"),

    DATE_RECEPTION("dateReception", "Date de réception"),
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
            val opt =
                Arrays.stream(entries.toTypedArray())
                    .filter { it: PeiCaracteristique -> it.code == stringValue }
                    .findFirst()
            if (opt.isPresent) return opt.get()

            throw IllegalArgumentException(
                "PeiCaracteristique : valeur '$stringValue' non trouvée",
            )
        }
    }
}
