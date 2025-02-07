package remocra.data

import remocra.data.enums.PeiCaracteristique

/**
 * Classe permettant de représenter un type d'attribut (défini par PeiCaracteristique) et la
 * valeur concernée (value)
 */
data class PeiCaracteristqueData(val caracteristique: PeiCaracteristique, val value: Any?)
