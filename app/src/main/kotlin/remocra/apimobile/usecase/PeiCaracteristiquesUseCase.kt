package remocra.apimobile.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.apimobile.repository.ReferentielRepository
import remocra.app.ParametresProvider
import remocra.data.PeiCaracteristqueData
import remocra.data.enums.ParametreEnum
import remocra.data.enums.PeiCaracteristique
import remocra.usecase.AbstractUseCase
import java.time.ZonedDateTime
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

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun getPeiCaracteristiquesMobile() =
        getPeiCaracteristiques(
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUE_PIBI.name),
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUE_PENA.name),
        )

    fun getPeiCaracteristiquesWeb() =
        getPeiCaracteristiques(
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUES_PIBI_TOOLTIP_WEB.name),
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUES_PENA_TOOLTIP_WEB.name),
        )

    private fun getPeiCaracteristiques(pibiSelectedCaracteristiques: String?, penaSelectedCaracteristiques: String?): Map<UUID, String> {
        val map =
            referentielRepository.getPeiCaracteristiques(
                pibiSelectedCaracteristiques.fromStringParameter(),
                penaSelectedCaracteristiques.fromStringParameter(),
            )

        // On transforme la liste de caractéristiques en HTML (liste à puces dans une DIV)
        val mapRetour: MutableMap<UUID, String> = HashMap()
        map.forEach { (key: UUID, value: List<PeiCaracteristqueData?>) ->
            mapRetour[key] = "<div><ul>"
            value.forEach { it ->
                if (it == null) {
                    return@forEach
                }
                mapRetour[key] += "<li>" + it.caracteristique.libelle + " : " + it.value.formatValue(it.caracteristique) + "</li>"
            }

            mapRetour[key] += "</ul></div>"
        }

        return mapRetour
    }

    private fun Any?.formatValue(peiCaracteristique: PeiCaracteristique): String {
        return when (peiCaracteristique) {
            PeiCaracteristique.DATE_RECEPTION -> if ((this == null)) "Non renseignée" else dateUtils.formatNaturelDateHeureMinute(this as ZonedDateTime)
            PeiCaracteristique.CAPACITE -> if ((this == null)) "Non renseignée" else "$this m3"
            PeiCaracteristique.DEBIT -> if ((this == null)) "Non renseigné" else "$this m3/h"
            else -> this?.toString() ?: ""
        }
    }

    private fun String?.fromStringParameter(): List<PeiCaracteristique> {
        if (this.isNullOrEmpty()) {
            return emptyList()
        }
        return objectMapper.readValue<List<PeiCaracteristique>>(this)
    }
}
