package remocra.utils

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.TypeEvent
import remocra.db.ModeleCourrierRepository
import remocra.db.RapportPersonnaliseRepository
import remocra.db.RequeteSqlRepository
import remocra.db.jooq.remocra.enums.TypeParametreEvenementComplement
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

    fun executeForModeleCourrier(userInfo: WrappedUserInfo, modeleCourrier: ModeleCourrierRepository.ModeleCourrierGenere) =
        execute(
            userInfo,
            dynamicForm = modeleCourrier.let {
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

    fun executeForEvenementComplement(userInfo: WrappedUserInfo, eventComplement: TypeEvent) =
        execute(
            userInfo,
            dynamicForm = DynamicFormGenere(
                dynamicFormId = eventComplement.id,
                dynamicFormLibelle = eventComplement.libelle,
                dynamicFormDescription = null,
                listeDynamicFormParametre = eventComplement.parameters.map {
                    DynamicFormParametreData(
                        dynamicFormParametreId = it.sousCategorieComplementId,
                        dynamicFormParametreCode = "",
                        dynamicFormParametreLibelle = it.sousCategorieComplementLibelle ?: "",
                        dynamicFormParametreSourceSql = it.sousCategorieComplementSql,
                        dynamicFormParametreDescription = "",
                        dynamicFormParametreSourceSqlId = it.sousCategorieComplementSqlId,
                        dynamicFormParametreSourceSqlLibelle = it.sousCategorieComplementSqlLibelle,
                        dynamicFormParametreValeurDefaut = it.sousCategorieComplementValeurDefaut,
                        dynamicFormParametreIsRequired = false,
                        dynamicFormParametreType = mapToRapportCourrier(it.sousCategorieComplementType),
                        dynamicFormParametreOrdre = 0,
                    )
                },
            ),
        )

    fun executeForRapportPerso(userInfo: WrappedUserInfo, rapportPersonnalise: RapportPersonnaliseRepository.RapportPersonnaliseGenere) =
        execute(
            userInfo,
            dynamicForm = rapportPersonnalise.let {
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

    private fun execute(userInfo: WrappedUserInfo, dynamicForm: DynamicFormGenere): DynamicFormWithParametre {
        // On s'occupe des paramètres
        val listeParametre = mutableListOf<DynamicFormParametreFront>()
        dynamicForm.listeDynamicFormParametre.map { parametre ->
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

        // On retourne l'objet avec toutes les infos
        return DynamicFormWithParametre(
            dynamicFormId = dynamicForm.dynamicFormId,
            dynamicFormLibelle = dynamicForm.dynamicFormLibelle,
            dynamicFormDescription = dynamicForm.dynamicFormDescription,
            listeParametre = listeParametre,
        )
    }

    // Mapper de TypeParametreEvenementComplement vers TypeParametreRapportCourrier
    fun mapToRapportCourrier(param: TypeParametreEvenementComplement): TypeParametreRapportCourrier {
        return when (param) {
            TypeParametreEvenementComplement.CHECKBOX_INPUT -> TypeParametreRapportCourrier.CHECKBOX_INPUT
            TypeParametreEvenementComplement.DATE_INPUT -> TypeParametreRapportCourrier.DATE_INPUT
            TypeParametreEvenementComplement.NUMBER_INPUT -> TypeParametreRapportCourrier.NUMBER_INPUT
            TypeParametreEvenementComplement.SELECT_INPUT -> TypeParametreRapportCourrier.SELECT_INPUT
            TypeParametreEvenementComplement.TEXT_INPUT -> TypeParametreRapportCourrier.TEXT_INPUT
        }
    }

    data class DynamicFormWithParametre(
        val dynamicFormId: UUID,
        val dynamicFormLibelle: String,
        val dynamicFormDescription: String?,
        val listeParametre: Collection<DynamicFormParametreFront>?,
    )

    data class DynamicFormParametreFront(
        val dynamicFormParametreId: UUID,
        val dynamicFormParametreLibelle: String?,
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
