package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.repository.ReferentielRepository
import remocra.app.ParametresProvider
import remocra.data.enums.PeiCaracteristique
import remocra.usecase.AbstractUseCase
import java.time.Instant
import java.util.UUID

/**
 * UseCase permettant de gérer la récupération dynamique des caractéristiques des PEI, et leur
 * transformation en vue d'être affichées par l'appli mobile
 */
class PeiCaracteristiquesUseCase : AbstractUseCase() {
    @Inject
    lateinit var referentielRepository: ReferentielRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    fun getPeiCaracteristiques(): Map<UUID, String> {
        return emptyMap()
//        val pibiSelectedCaracteristiques = parametresProvider.getParametreString(GlobalConstants.PARAMETRE_CARACTERISTIQUE_PIBI)
//        val penaSelectedCaracteristiques = parametresProvider.getParametreString(GlobalConstants.PARAMETRE_CARACTERISTIQUE_PENA)
//
//
//        val map: Map<Long, List<PeiCaracteristiquePojo>> =
//                referentielRepository.getPeiCaracteristiques(
//                        pibiSelectedCaracteristiques.fromStringParameter(),
//                        penaSelectedCaracteristiques.fromStringParameter()
//                )
//
//        // On transforme la liste de caractéristiques en HTML (liste à puces dans une DIV)
//        val mapRetour: MutableMap<Long, String> = HashMap()
//        map.forEach { (key: Long, value: List<PeiCaracteristiquePojo>) ->
//            mapRetour[key] = "<div><ul>" + value.map { it: PeiCaracteristiquePojo ->
//                (("<li>"
//                        + it.caracteristique.libelle
//                        ) + " : "
//                        + formatValue(it.value, it.caracteristique)
//                        + "</li>")
//            } + "</ul></div>"
//
//        }
//
//        return mapRetour
    }

    private fun Any?.formatValue(peiCaracteristique: PeiCaracteristique): String {
        return when (peiCaracteristique) {
            PeiCaracteristique.DATE_RECEPTION -> if ((this == null)) "Non renseignée" else dateUtils.formatNaturel(this as Instant)
            PeiCaracteristique.CAPACITE -> if ((this == null)) "Non renseignée" else "$this m3"
            PeiCaracteristique.DEBIT -> if ((this == null)) "Non renseigné" else "$this m3/h"
            else -> if ((this == null)) "" else this.toString()
        }
    }

    private fun String?.fromStringParameter(): List<PeiCaracteristique> {
        if (this.isNullOrEmpty()) {
            return emptyList()
        }
        return this.split(",").map { it.trim() }.filter { it.isNotEmpty() }.map { PeiCaracteristique.fromString(it) }
    }
}

/**
 * Classe permettant de représenter un type d'attribut (défini par PeiCaracteristique) et la
 * valeur concernée (value)
 */
data class PeiCaracteristiquePojo(val caracteristique: PeiCaracteristique, val value: Any)
