package remocra.usecase.courrier

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.GlobalData
import remocra.data.enums.ErrorType
import remocra.db.CisCommuneRepository
import remocra.db.CommuneRepository
import remocra.db.GestionnaireRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.TypeParametreCourrier
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

    fun execute(userInfo: UserInfo?): MutableList<ModeleCourrierWithParametre> {
        if (userInfo == null) {
            throw RemocraResponseException(ErrorType.MODELE_COURRIER_DROIT_FORBIDDEN)
        }
        val allModeleCourrier = modeleCourrierRepository.getAll(userInfo.utilisateurId)
        val mapParametreByCourrier = modeleCourrierRepository.getParametresByModele()

        val listeWithParametres = mutableListOf<ModeleCourrierWithParametre>()

        // Ceux qui n'ont pas de paramètres
        val courriersSansParametre = allModeleCourrier.filter { !mapParametreByCourrier.keys.contains(it) }

        courriersSansParametre.forEach {
            listeWithParametres.add(
                ModeleCourrierWithParametre(
                    modeleCourrier = it,
                    listParametres = null,
                ),
            )
        }
        val listCommune = communeRepository.getCommuneForSelect().map {
            GlobalData.IdCodeLibelleLienData(
                it.id,
                it.code,
                it.libelle,
                null,
            )
        }
        val listGestionnaire = gestionnaireRepository.getAll().map {
            GlobalData.IdCodeLibelleLienData(
                it.id,
                it.code,
                it.libelle,
                null,
            )
        }

        val listCisWithCommune = cisCommuneRepository.getCisCommune()

        mapParametreByCourrier.forEach { courrierWithParametres ->
            val listParametreCourrier = mutableListOf<ParametreCourrier>()
            courrierWithParametres.value.sortedBy { it.modeleCourrierParametreOrdre }.map { parametre ->
                listParametreCourrier.add(
                    when (parametre.modeleCourrierParametreTypeParametreCourrier) {
                        TypeParametreCourrier.COMMUNE_ID -> {
                            ParametreCourrier(
                                nameField = parametre.modeleCourrierParametreTypeParametreCourrier.name,
                                label = parametre.modeleCourrierParametreLibelle,
                                liste = listCommune,
                                description = parametre.modeleCourrierParametreDescription,
                                typeComposant = TypeComposant.SELECT_INPUT,
                                conditionToDisplay = if (courrierWithParametres.value
                                        .firstOrNull { it.modeleCourrierParametreTypeParametreCourrier == TypeParametreCourrier.IS_ONLY_PUBLIC } != null
                                ) {
                                    ConditionToDisplay(
                                        nameField = TypeParametreCourrier.IS_ONLY_PUBLIC.name,
                                        valeurAttendue = ValeurAttendue(
                                            Operation.EGAL,
                                            true,
                                        ),
                                    )
                                } else {
                                    null
                                },
                                defaultValue = null,
                            )
                        }
                        TypeParametreCourrier.GESTIONNAIRE_ID -> {
                            ParametreCourrier(
                                nameField = parametre.modeleCourrierParametreTypeParametreCourrier.name,
                                label = parametre.modeleCourrierParametreLibelle,
                                liste = listGestionnaire,
                                description = parametre.modeleCourrierParametreDescription,
                                typeComposant = TypeComposant.SELECT_INPUT,
                                conditionToDisplay = if (courrierWithParametres.value
                                        .firstOrNull { it.modeleCourrierParametreTypeParametreCourrier == TypeParametreCourrier.IS_ONLY_PUBLIC } != null
                                ) {
                                    ConditionToDisplay(
                                        nameField = TypeParametreCourrier.IS_ONLY_PUBLIC.name,
                                        valeurAttendue = ValeurAttendue(
                                            Operation.EGAL,
                                            false,
                                        ),
                                    )
                                } else {
                                    null
                                },
                                defaultValue = null,
                            )
                        }
                        TypeParametreCourrier.CIS_ID -> {
                            ParametreCourrier(
                                nameField = parametre.modeleCourrierParametreTypeParametreCourrier.name,
                                label = parametre.modeleCourrierParametreLibelle,
                                liste = listCisWithCommune,
                                description = parametre.modeleCourrierParametreDescription,
                                typeComposant = TypeComposant.SELECT_INPUT,
                                conditionToDisplay =
                                ConditionToDisplay(
                                    nameField = TypeParametreCourrier.COMMUNE_ID.name,
                                    valeurAttendue = ValeurAttendue(
                                        Operation.DIFFERENT,
                                        null,
                                    ),
                                ),
                                defaultValue = null,
                                nameLienField = TypeParametreCourrier.COMMUNE_ID.name,
                            )
                        }
                        TypeParametreCourrier.IS_ONLY_PUBLIC,
                        TypeParametreCourrier.IS_EPCI,
                        -> {
                            ParametreCourrier(
                                nameField = parametre.modeleCourrierParametreTypeParametreCourrier.name,
                                label = parametre.modeleCourrierParametreLibelle,
                                liste = null,
                                description = parametre.modeleCourrierParametreDescription,
                                typeComposant = TypeComposant.CHECKBOX_INPUT,
                                defaultValue = true,
                            )
                        }
                        TypeParametreCourrier.PROFIL_UTILISATEUR_ID -> TODO()
                        TypeParametreCourrier.ANNEE,
                        TypeParametreCourrier.EXPEDITEUR_GRADE,
                        TypeParametreCourrier.EXPEDITEUR_STATUT,
                        TypeParametreCourrier.REFERENCE,
                        -> {
                            ParametreCourrier(
                                nameField = parametre.modeleCourrierParametreTypeParametreCourrier.name,
                                label = parametre.modeleCourrierParametreLibelle,
                                description = parametre.modeleCourrierParametreDescription,
                                liste = null,
                                typeComposant = TypeComposant.TEXT_INPUT,
                                defaultValue = "",
                            )
                        }
                    },
                )
            }

            listeWithParametres.add(
                ModeleCourrierWithParametre(
                    modeleCourrier = courrierWithParametres.key,
                    listParametres = listParametreCourrier,
                ),
            )
        }

        return listeWithParametres
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
