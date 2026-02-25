package remocra.usecase.pei

import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.DataTableau
import remocra.data.Params
import remocra.data.enums.ParametreEnum
import remocra.data.enums.PeiColonnes
import remocra.db.PeiRepository
import remocra.db.TourneeRepository
import remocra.usecase.AbstractUseCase

class ListPeiUseCase @Inject constructor(
    private val parametresProvider: ParametresProvider,
    private val peiRepository: PeiRepository,
    private val tourneeRepository: TourneeRepository,
) : AbstractUseCase() {

    private val logger: Logger = LoggerFactory.getLogger(ListPeiUseCase::class.java)

    fun execute(params: Params<PeiRepository.Filter, PeiRepository.Sort>, userInfo: WrappedUserInfo): DataTableau<PeiRepository.PeiForTableau> {
        // On récupère les colonnes qui sont utile en fonction du paramètre
        val valueString = parametresProvider.getParametreString(ParametreEnum.PEI_COLONNES.name)

        // C'est une liste sous la forme ["colonne1", "colonne2", ...]
        val colonnes = valueString?.let {
            it.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
        } ?: emptyList()

        // ça doit être une liste de PEI_COLONNES, on vérifie que les valeurs sont correctes
        val colonnesPei: Set<PeiColonnes> = colonnes.mapNotNullTo(mutableSetOf()) {
            try {
                PeiColonnes.valueOf(it)
            } catch (_: IllegalArgumentException) {
                logger.warn("La colonne $it n'est pas une colonne valide pour les PEI, elle sera ignorée")
                null
            }
        }

        // On va chercher les informations des PEI avec uniquement les colonnes demandées
        val pei = peiRepository.getListePei(params, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin, colonnesPei)

        // On va chercher les tournées que des PEI renvoyer pour éviter que la requête soit trop longue, et on les associe aux PEI
        val idPei = pei.map { it.peiId }
        val tournees = tourneeRepository.getListTourneeLibelleByListPei(idPei)

        return DataTableau(
            list = pei.map {
                it.copy(tourneeLibelle = tournees[it.peiId])
            },
            count = peiRepository.countListePei(params.filterBy, userInfo.zoneCompetence?.zoneIntegrationId, userInfo.isSuperAdmin, colonnesPei),
        )
    }
}
