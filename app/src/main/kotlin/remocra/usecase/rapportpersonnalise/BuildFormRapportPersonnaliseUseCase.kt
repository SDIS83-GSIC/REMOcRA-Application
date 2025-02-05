package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
import remocra.data.IdLibelleRapportPersonnalise
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Permet de récupérer tous les rapports personnalisés et les paramètres de ces derniers
 * Ce usecase permettra de fabriquer les formulaires dynamiquement dans le front
 */
class BuildFormRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var rapportPersonnaliseUtils: RapportPersonnaliseUtils

    fun execute(userInfo: UserInfo?): MutableList<RapportPersonnaliseWithParametre> {
        if (userInfo == null) {
            throw ForbiddenException()
        }

        // On va chercher les rapports personnalisés
        val listeRapportPerso = rapportPersonnaliseRepository.getListeRapportPersonnalise(userInfo.utilisateurId, userInfo.isSuperAdmin)

        // On s'occupe des paramètres
        val listeRapportPersoWithParametres = mutableListOf<RapportPersonnaliseWithParametre>()
        listeRapportPerso.forEach { rp ->
            val listeParametre = mutableListOf<RapportPersonnaliseParametreFront>()
            rp.listeRapportPersonnaliseParametre.map { parametre ->
                // -> Si SELECT_INPUT alors on build la requête et on retourne une liste
                var listeSelectInput: List<IdLibelleRapportPersonnalise>? = null
                if (parametre.rapportPersonnaliseParametreType == TypeParametreRapportPersonnalise.SELECT_INPUT) {
                    val requeteModifiee = rapportPersonnaliseUtils.formatParametreRequeteSql(userInfo, parametre.rapportPersonnaliseParametreSourceSql)
                    listeSelectInput = rapportPersonnaliseRepository.executeSqlParametre(requeteModifiee!!)
                }

                listeParametre.add(
                    RapportPersonnaliseParametreFront(
                        rapportPersonnaliseParametreId = parametre.rapportPersonnaliseParametreId,
                        rapportPersonnaliseParametreLibelle = parametre.rapportPersonnaliseParametreLibelle,
                        rapportPersonnaliseParametreCode = parametre.rapportPersonnaliseParametreCode,
                        listeSelectInput = listeSelectInput,
                        rapportPersonnaliseParametreDescription = parametre.rapportPersonnaliseParametreDescription,
                        rapportPersonnaliseParametreValeurDefaut = parametre.rapportPersonnaliseParametreValeurDefaut,
                        rapportPersonnaliseParametreIsRequired = parametre.rapportPersonnaliseParametreIsRequired,
                        rapportPersonnaliseParametreType = parametre.rapportPersonnaliseParametreType,
                    ),
                )
            }
            listeRapportPersoWithParametres.add(
                RapportPersonnaliseWithParametre(
                    rapportPersonnaliseId = rp.rapportPersonnaliseId,
                    rapportPersonnaliseLibelle = rp.rapportPersonnaliseLibelle,
                    rapportPersonnaliseDescription = rp.rapportPersonnaliseDescription,
                    listeParametre = listeParametre,
                ),
            )
        }

        // On retourne l'objet avec toutes les infos
        return listeRapportPersoWithParametres
    }

    data class RapportPersonnaliseWithParametre(
        val rapportPersonnaliseId: UUID,
        val rapportPersonnaliseLibelle: String,
        val rapportPersonnaliseDescription: String?,
        val listeParametre: Collection<RapportPersonnaliseParametreFront>,
    )

    data class RapportPersonnaliseParametreFront(
        val rapportPersonnaliseParametreId: UUID,
        val rapportPersonnaliseParametreLibelle: String,
        val rapportPersonnaliseParametreCode: String,
        val listeSelectInput: List<IdLibelleRapportPersonnalise>?,
        val rapportPersonnaliseParametreDescription: String?,
        val rapportPersonnaliseParametreValeurDefaut: String?,
        val rapportPersonnaliseParametreIsRequired: Boolean,
        val rapportPersonnaliseParametreType: TypeParametreRapportPersonnalise,
    )
}
