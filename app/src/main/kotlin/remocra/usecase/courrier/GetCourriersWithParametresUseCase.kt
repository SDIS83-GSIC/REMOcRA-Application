package remocra.usecase.courrier

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.GlobalData
import remocra.data.enums.ErrorType
import remocra.db.CisCommuneRepository
import remocra.db.CommuneRepository
import remocra.db.GestionnaireRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase

/**
 * Retourne tous les modèles de courriers avec leurs paramètres
 */
class GetCourriersWithParametresUseCase : AbstractUseCase() {
    @Inject lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject lateinit var cisCommuneRepository: CisCommuneRepository

    fun execute(userInfo: UserInfo?): Collection<ModeleCourrierWithParametre> {
        if (userInfo == null) {
            throw RemocraResponseException(ErrorType.MODELE_COURRIER_DROIT_FORBIDDEN)
        }

        // TODO
        return listOf()
    }

    data class ModeleCourrierWithParametre(
        val modeleCourrier: ModeleCourrier,
        val listParametres: Collection<ParametreCourrier>?,
    )

    /**
     * @property nameField : Le nom utilisé dans les inputs et qui nous sera retourné
     * @property label : Le nom à afficher (stocké en base)
     * @property description : La description du paramètre qui s'affichera sous forme de tooltip si elle n'est pas nulle
     * @property liste : Si c'est une liste déroulante, on passe l'id, le code , le libellé et l'id de laison. Les paramètres peuvent
     * se compléter. Par exemple, pour le CIS, on ne veut afficher que les CIS de la commune sélectionnée
     * @property typeComposant : Type de composant utilisé dans le front
     * @property conditionToDisplay : Condition à respecter pour que le paramètre soit affiché
     * @property defaultValue : valeur par défaut
     * @property nameLienField : Le nom du champ de lien utilisé dans le front
     */
    data class ParametreCourrier(
        val nameField: String,
        val label: String,
        val description: String?,
        val liste: Collection<GlobalData.IdCodeLibelleLienData>?,
        val typeComposant: TypeComposant,
        val conditionToDisplay: ConditionToDisplay? = null,
        val defaultValue: Any?,
        val nameLienField: String? = null,
    )

    /**
     * Les paramètres peuvent dépendre des uns et des autres. On passe donc le nameField du paramètre et
     * la valeur attendue pour que notre paramètre soit affiché. Il faut aussi préciser le type d'opération si égal ou différent
     */
    data class ConditionToDisplay(
        val nameField: String,
        val valeurAttendue: ValeurAttendue,
    )

    data class ValeurAttendue(
        val operation: Operation,
        val valeurAttendue: Any?,
    )

    enum class Operation {
        DIFFERENT,
        EGAL,
    }

    enum class TypeComposant {
        TEXT_INPUT,
        SELECT_INPUT,
        CHECKBOX_INPUT,
    }
}
