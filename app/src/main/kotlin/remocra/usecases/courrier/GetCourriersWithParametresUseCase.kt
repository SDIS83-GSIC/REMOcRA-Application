package remocra.usecases.courrier

import com.google.inject.Inject
import remocra.data.GlobalData
import remocra.db.CommuneRepository
import remocra.db.GestionnaireRepository
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.TypeParametreCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier

/**
 * Retourne tous les modèles de courriers avec leurs paramètres
 */
class GetCourriersWithParametresUseCase {
    @Inject lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var gestionnaireRepository: GestionnaireRepository

    fun execute(): MutableList<ModeleCourrierWithParametre> {
        // TODO prendre en compte le profil droit de l'utilisateur
        val allModeleCourrier = modeleCourrierRepository.getAll()
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
        val listCommune = communeRepository.getCommuneForSelect()
        val listGestionnaire = gestionnaireRepository.getAll()

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
                                        valeurAttendue = true,
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
                                        valeurAttendue = false,
                                    )
                                } else {
                                    null
                                },
                                defaultValue = null,
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
                        TypeParametreCourrier.CIS_ID -> TODO()
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

    data class ParametreCourrier(
        val nameField: String, // Nom utilisé dans les input dans le front
        val label: String, // Le label à afficher
        val description: String?,
        val liste: Collection<GlobalData.IdCodeLibelleData>?, // Si c'est une liste déroulante
        val typeComposant: TypeComposant, // TextArea, CheckBox ...
        val conditionToDisplay: ConditionToDisplay? = null,
        val defaultValue: Any?,
    )

    /**
     * Les paramètres peuvent dépendre des uns et des autres. On passe donc le nameField du paramètre et la valeur attendue pour que notre paramètre soit affiché
     */
    data class ConditionToDisplay(
        val nameField: String,
        val valeurAttendue: Any,
    )

    enum class TypeComposant {
        TEXT_INPUT,
        SELECT_INPUT,
        CHECKBOX_INPUT,
    }
}
