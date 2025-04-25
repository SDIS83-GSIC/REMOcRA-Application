package remocra.usecase.messagelongueindispo

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ParametreEnum
import remocra.db.PeiRepository
import remocra.db.TracabiliteRepository
import remocra.db.TypeOrganismeRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.getListOfString
import java.time.Period
import java.util.UUID

class GetMessagePeiLongueIndispoUseCase : AbstractUseCase() {

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var typeOrganismeRepository: TypeOrganismeRepository

    @Inject
    lateinit var tracabiliteRepository: TracabiliteRepository

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var objectMapper: ObjectMapper

    companion object {
        const val PLACEHOLDER_MOIS = "#MOIS#"
        const val PLACEHOLDER_JOURS = "#JOURS#"
    }

    fun execute(userInfo: WrappedUserInfo): Message? {
        val message = parametresProvider.getParametreString(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_MESSAGE.name)
            ?: return null
        val nombreJoursIndispo = parametresProvider.getParametreInt(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_JOURS.name)
            ?: return null

        // On regarde si l'utilisateur connecté a un type organisme compris dans ceux à notifier
        val listePeiId = getListePeiAlerte(userInfo)

        if (listePeiId.isNullOrEmpty()) {
            return null
        }

        // Puis on remplace dans le template le nombre de mois et le nombre de jours
        val period = Period.between(
            dateUtils.now().minusDays(nombreJoursIndispo.toLong()).toLocalDate(),
            dateUtils.now().toLocalDate(),
        )
        if (listePeiId.isNullOrEmpty()) {
            return null
        }

        // Puis, on retourne le message pour le warning de la page d'accueil
        // et les valeurs "mois" et "jour" pour le titre du tableau.
        return Message(
            message = message.replace(PLACEHOLDER_MOIS, period.months.toString())
                .replace(PLACEHOLDER_JOURS, period.days.toString()),
            nbMois = period.months,
            nbJour = period.days,
        )
    }
    data class Message(
        val message: String,
        val nbMois: Int,
        val nbJour: Int,
    )

    fun getListePeiAlerte(userInfo: WrappedUserInfo): Set<UUID>? {
        val nombreJoursIndispo = parametresProvider.getParametreInt(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_JOURS.name)
            ?: return null

        // On regarde si l'utilisateur connecté a un type organisme compris dans ceux à notifier
        if (!userInfo.isSuperAdmin) {
            val typeOrganismeUtilisateurConnecte = typeOrganismeRepository
                .getUserTypeOrganisme(userInfo.organismeId!!)

            val listTypeOrganisme = parametresProvider.get().mapParametres
                .getListOfString(ParametreEnum.PEI_LONGUE_INDISPONIBILITE_TYPE_ORGANISME.name, objectMapper) ?: listOf()

            if (!listTypeOrganisme.contains(typeOrganismeUtilisateurConnecte)) {
                return null
            }
        }

        // On va ensuite chercher les PEI qui sont indisponibles depuis X jours
        val listePeiIdIndispos = peiRepository.getPeiIdIndisponibles(
            userInfo.zoneCompetence?.zoneIntegrationId,
            userInfo.isSuperAdmin,
        )

        // On remonte ceux qui ont un statut disponible entre aujourd'hui - X jours et aujourd'hui
        val listePeiIdDisponible = tracabiliteRepository.getPeiIdDisponibles(nombreJoursIndispo)

        // On ne prend que les indisponibles qui n'ont pas été dispo entre aujourd'hui - X jours et aujourd'hui
        return listePeiIdIndispos.minus(listePeiIdDisponible).toSet()
    }
}
