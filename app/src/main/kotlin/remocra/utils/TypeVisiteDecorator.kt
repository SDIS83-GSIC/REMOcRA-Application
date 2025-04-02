package remocra.utils

import remocra.db.jooq.remocra.enums.TypeVisite

/**
 * Permet de retourner le libellé correspondant au type Visite passé en paramètre
 */
fun getLibelleTypeVisite(typeVisite: TypeVisite) =
    when (typeVisite) {
        TypeVisite.RECEPTION -> "Visite de réception"
        TypeVisite.RECO_INIT -> "Reconnaissance opérationnelle initiale"
        TypeVisite.CTP -> "Contrôle technique périodique"
        TypeVisite.ROP -> "Reconnaissance opérationnelle périodique"
        TypeVisite.NP -> "Non programmée"
    }
