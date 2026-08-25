package remocra.apimobile.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.inject.Inject
import remocra.apimobile.repository.ReferentielRepository
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.PeiCaracteristqueData
import remocra.data.enums.ParametreEnum
import remocra.data.enums.PeiCaracteristique
import remocra.db.TourneeRepository
import remocra.usecase.AbstractUseCase
import java.time.ZonedDateTime
import java.util.UUID

/**
 * UseCase permettant de gérer la récupération dynamique des caractéristiques des PEI, et leur
 * transformation en vue d'être affichées par l'appli mobile
 */
class PeiCaracteristiquesUseCase
@Inject
constructor(
    private val referentielRepository: ReferentielRepository,
    private val tourneeRepository: TourneeRepository,
    private val parametresProvider: ParametresProvider,
    private val objectMapper: ObjectMapper,
) :
    AbstractUseCase() {
    fun getPeiCaracteristiquesMobile(userInfo: WrappedUserInfo) =
        getPeiCaracteristiques(
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUE_PIBI.name),
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUE_PENA.name),
            userInfo,
        )

    fun getPeiCaracteristiquesWeb(userInfo: WrappedUserInfo) =
        getPeiCaracteristiques(
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUES_PIBI_TOOLTIP_WEB.name),
            parametresProvider.getParametreString(ParametreEnum.CARACTERISTIQUES_PENA_TOOLTIP_WEB.name),
            userInfo,
        )

    /**
     * Enrichit les résultats avec les libellés de tournée
     */
    private fun enrichTourneesInResults(
        result: Map<UUID, List<PeiCaracteristqueData?>>,
        tournees: Map<UUID, String>,
    ): Map<UUID, List<PeiCaracteristqueData?>> {
        if (tournees.isEmpty()) return result

        return result.mapValues { (peiId, caracteristiques) ->
            caracteristiques.map { car ->
                if (car?.caracteristique == PeiCaracteristique.TOURNEE) {
                    car.copy(value = tournees[peiId] ?: car.value)
                } else {
                    car
                }
            }
        }
    }

    private fun getPeiCaracteristiques(pibiSelectedCaracteristiques: String?, penaSelectedCaracteristiques: String?, userInfo: WrappedUserInfo): Map<UUID, String> {
        val pibiCaracteristiques = pibiSelectedCaracteristiques.fromStringParameter()
        val penaCaracteristiques = penaSelectedCaracteristiques.fromStringParameter()

        var map =
            referentielRepository.getPeiCaracteristiques(
                pibiCaracteristiques,
                penaCaracteristiques,
                userInfo,
            )

        val hasTournee =
            pibiCaracteristiques.contains(PeiCaracteristique.TOURNEE) ||
                penaCaracteristiques.contains(PeiCaracteristique.TOURNEE)

        if (hasTournee) {
            map =
                enrichTourneesInResults(
                    map,
                    tourneeRepository.getListTourneeLibelleByListPeiAndAffiliatedOrganisme(map.keys.toList(), userInfo),
                )
        }

        // On transforme la liste de caractéristiques en HTML (liste à puces dans une DIV)
        val mapRetour: MutableMap<UUID, String> = HashMap()
        map.forEach { (key: UUID, value: List<PeiCaracteristqueData?>) ->
            mapRetour[key] = "<div><ul>"
            value.forEach {
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
            PeiCaracteristique.DATE_RECEPTION, PeiCaracteristique.DATE_ROI, PeiCaracteristique.DATE_ROP, PeiCaracteristique.DATE_CTP -> if ((this == null)) "Non renseignée" else dateUtils.formatNaturelDateHeureMinute(this as ZonedDateTime)
            PeiCaracteristique.CAPACITE -> if ((this == null)) "Non renseignée" else "$this"
            PeiCaracteristique.DEBIT -> if ((this == null)) "Non renseigné" else "$this m³/h"
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
