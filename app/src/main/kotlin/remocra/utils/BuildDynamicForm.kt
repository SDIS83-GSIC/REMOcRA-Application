package remocra.utils

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
import remocra.db.ModeleCourrierRepository
import remocra.db.RapportPersonnaliseRepository
import remocra.db.RequeteSqlRepository
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Permet de renvoyer dynamiquement les paramètres d'un formulaire
 */
class BuildDynamicForm : AbstractUseCase() {

    @Inject
    private lateinit var requeteSqlRepository: RequeteSqlRepository

    @Inject
    private lateinit var requestUtils: RequestUtils

    fun executeForModeleCourrier(userInfo: UserInfo?, listModeleCourrier: Collection<ModeleCourrierRepository.ModeleCourrierGenere>) =
        execute(
            userInfo,
            listeDynamicForm = listModeleCourrier.map {
                DynamicFormGenere(
                    dynamicFormId = it.modeleCourrierId,
                    dynamicFormLibelle = it.modeleCourrierLibelle,
                    dynamicFormDescription = it.modeleCourrierDescription,
                    listeDynamicFormParametre = it.listeModeleCourrierParametre.map {
                        DynamicFormParametreData(
                            dynamicFormParametreId = it.modeleCourrierParametreId,
                            dynamicFormParametreCode = it.modeleCourrierParametreCode,
                            dynamicFormParametreLibelle = it.modeleCourrierParametreLibelle,
                            dynamicFormParametreSourceSql = it.modeleCourrierParametreSourceSql,
                            dynamicFormParametreDescription = it.modeleCourrierParametreDescription,
                            dynamicFormParametreSourceSqlId = it.modeleCourrierParametreSourceSqlId,
                            dynamicFormParametreSourceSqlLibelle = it.modeleCourrierParametreSourceSqlLibelle,
                            dynamicFormParametreValeurDefaut = it.modeleCourrierParametreValeurDefaut,
                            dynamicFormParametreIsRequired = it.modeleCourrierParametreIsRequired,
                            dynamicFormParametreType = it.modeleCourrierParametreType,
                            dynamicFormParametreOrdre = it.modeleCourrierParametreOrdre,
                        )
                    },
                )
            },
        )

    fun executeForRapportPerso(userInfo: UserInfo?, listRapportPersonnalise: Collection<RapportPersonnaliseRepository.RapportPersonnaliseGenere>) =
        execute(
            userInfo,
            listeDynamicForm = listRapportPersonnalise.map {
                DynamicFormGenere(
                    dynamicFormId = it.rapportPersonnaliseId,
                    dynamicFormLibelle = it.rapportPersonnaliseLibelle,
                    dynamicFormDescription = it.rapportPersonnaliseDescription,
                    listeDynamicFormParametre = it.listeRapportPersonnaliseParametre.map {
                        DynamicFormParametreData(
                            dynamicFormParametreId = it.rapportPersonnaliseParametreId,
                            dynamicFormParametreCode = it.rapportPersonnaliseParametreCode,
                            dynamicFormParametreLibelle = it.rapportPersonnaliseParametreLibelle,
                            dynamicFormParametreSourceSql = it.rapportPersonnaliseParametreSourceSql,
                            dynamicFormParametreDescription = it.rapportPersonnaliseParametreDescription,
                            dynamicFormParametreSourceSqlId = it.rapportPersonnaliseParametreSourceSqlId,
                            dynamicFormParametreSourceSqlLibelle = it.rapportPersonnaliseParametreSourceSqlLibelle,
                            dynamicFormParametreValeurDefaut = it.rapportPersonnaliseParametreValeurDefaut,
                            dynamicFormParametreIsRequired = it.rapportPersonnaliseParametreIsRequired,
                            dynamicFormParametreType = it.rapportPersonnaliseParametreType,
                            dynamicFormParametreOrdre = it.rapportPersonnaliseParametreOrdre,
                        )
                    },
                )
            },
        )

    private fun execute(userInfo: UserInfo?, listeDynamicForm: Collection<DynamicFormGenere>): Collection<DynamicFormWithParametre> {
        if (userInfo == null) {
            throw ForbiddenException()
        }

        // On s'occupe des paramètres
        val listeDynamicFormWithParametres = mutableListOf<DynamicFormWithParametre>()
        listeDynamicForm.forEach { rp ->
            val listeParametre = mutableListOf<DynamicFormParametreFront>()
            rp.listeDynamicFormParametre.map { parametre ->
                // -> Si SELECT_INPUT alors on build la requête et on retourne une liste
                var listeSelectInput: List<IdLibelleDynamicForm>? = null
                if (parametre.dynamicFormParametreType == TypeParametreRapportCourrier.SELECT_INPUT) {
                    val requeteModifiee = requestUtils.replaceGlobalParameters(userInfo, parametre.dynamicFormParametreSourceSql!!)
                    listeSelectInput = requeteSqlRepository.executeSqlParametre(requeteModifiee)
                }

                listeParametre.add(
                    DynamicFormParametreFront(
                        dynamicFormParametreId = parametre.dynamicFormParametreId,
                        dynamicFormParametreLibelle = parametre.dynamicFormParametreLibelle,
                        dynamicFormParametreCode = parametre.dynamicFormParametreCode,
                        listeSelectInput = listeSelectInput,
                        dynamicFormParametreDescription = parametre.dynamicFormParametreDescription,
                        dynamicFormParametreValeurDefaut = parametre.dynamicFormParametreValeurDefaut,
                        dynamicFormParametreIsRequired = parametre.dynamicFormParametreIsRequired,
                        dynamicFormParametreType = parametre.dynamicFormParametreType,
                    ),
                )
            }
            listeDynamicFormWithParametres.add(
                DynamicFormWithParametre(
                    dynamicFormId = rp.dynamicFormId,
                    dynamicFormLibelle = rp.dynamicFormLibelle,
                    dynamicFormDescription = rp.dynamicFormDescription,
                    listeParametre = listeParametre,
                ),
            )
        }

        // On retourne l'objet avec toutes les infos
        return listeDynamicFormWithParametres
    }

    data class DynamicFormWithParametre(
        val dynamicFormId: UUID,
        val dynamicFormLibelle: String,
        val dynamicFormDescription: String?,
        val listeParametre: Collection<DynamicFormParametreFront>,
    )

    data class DynamicFormParametreFront(
        val dynamicFormParametreId: UUID,
        val dynamicFormParametreLibelle: String,
        val dynamicFormParametreCode: String,
        val listeSelectInput: List<IdLibelleDynamicForm>?,
        val dynamicFormParametreDescription: String?,
        val dynamicFormParametreValeurDefaut: String?,
        val dynamicFormParametreIsRequired: Boolean,
        val dynamicFormParametreType: TypeParametreRapportCourrier,
    )

    data class IdLibelleDynamicForm(
        val id: String,
        val libelle: String?,
    )

    data class DynamicFormGenere(
        val dynamicFormId: UUID,
        val dynamicFormLibelle: String,
        val dynamicFormDescription: String?,
        val listeDynamicFormParametre: Collection<DynamicFormParametreData>,
    )

    data class DynamicFormParametreData(
        val dynamicFormParametreId: UUID = UUID.randomUUID(),
        val dynamicFormParametreCode: String,
        val dynamicFormParametreLibelle: String,
        val dynamicFormParametreSourceSql: String?,
        val dynamicFormParametreDescription: String?,
        val dynamicFormParametreSourceSqlId: String?,
        val dynamicFormParametreSourceSqlLibelle: String?,
        val dynamicFormParametreValeurDefaut: String?,
        val dynamicFormParametreIsRequired: Boolean,
        val dynamicFormParametreType: TypeParametreRapportCourrier,
        val dynamicFormParametreOrdre: Int,
    )
}
