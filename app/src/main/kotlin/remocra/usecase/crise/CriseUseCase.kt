package remocra.usecase.crise

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementSousCategorieDetails
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.TypeEvent
import remocra.db.CommuneRepository
import remocra.db.CriseRepository
import remocra.db.CriseRepository.ToponymieResult
import remocra.db.CriseRepository.TypeCriseComplete
import remocra.db.EvenementRepository
import remocra.db.EvenementSousCategorieRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.BuildDynamicForm
import remocra.utils.toGeomFromText
import java.util.UUID

class CriseUseCase : AbstractUseCase() {
    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var evenementRepository: EvenementRepository

    @Inject lateinit var evenementSousCategorieRepository: EvenementSousCategorieRepository

    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var appSettings: AppSettings

    @Inject private lateinit var buildDynamicForm: BuildDynamicForm

    fun getTypeCriseForSelect(): Collection<TypeCriseComplete> = criseRepository.getCriseForSelect()

    /**
     * Récupère toutes les géométries des communes associées à la crise spécifiée par son identifiant.
     *
     * @param criseId L'identifiant de la crise pour laquelle les géométries des communes doivent être récupérées.
     * @return La liste des géométries des communes associées à la crise, ou une liste vide si aucune géométrie n'est trouvée.
     */
    fun getCommuneGeometriesByCrise(criseId: UUID): List<Geometry> {
        val communeIds = criseRepository.getCrise(criseId).listeCommuneId ?: return emptyList()
        return communeIds.map { communeRepository.getById(it).communeGeometrie }
    }

    fun getCriseForMerge(): Collection<CriseRepository.CriseMerge> {
        return criseRepository.getCriseForMerge()
    }

    fun groupTypeEvent(
        events: Collection<EvenementSousCategorieDetails>,
    ): List<EvenementSousCategorieDetails> {
        return events
            .groupBy { it.evenementSousCategorieId to it.evenementSousCategorieLibelle }
            .map { (cle, eventList) ->
                val (sousCategorieEvenementId, sousCategorieEvenementLibelle) = cle
                EvenementSousCategorieDetails(
                    evenementSousCategorieId = sousCategorieEvenementId,
                    evenementSousCategorieCode = eventList.firstNotNullOfOrNull { it.evenementSousCategorieCode },
                    evenementSousCategorieLibelle = sousCategorieEvenementLibelle,
                    evenementSousCategorieTypeGeometrie = eventList.firstNotNullOfOrNull { it.evenementSousCategorieTypeGeometrie },
                    evenementSousCategorieComplement = eventList.flatMap { it.evenementSousCategorieComplement.orEmpty() },
                )
            }
    }

    fun getSousCategorieEvenement(
        userInfo: WrappedUserInfo,
        evenementSousCategorieId: UUID?,
    ): List<EvenementSousCategorieDetails> {
        // si (evenementSousCategorieId != null), ça veut dire qu'on a une géométrie (ex : hélitreuillage => sous catégorie d'événement)
        return if (evenementSousCategorieId != null) {
            groupTypeEvent(
                buildSousCategorieEvenementDetails(
                    userInfo,
                    evenementSousCategorieRepository.getById(evenementSousCategorieId),
                ),
            )
        } else {
            groupTypeEvent(
                evenementRepository.getTypeEventByGeometry(hasGeometry = false)
                    .map { event ->
                        buildSousCategorieEvenementDetails(
                            userInfo,
                            evenementSousCategorieRepository.getById(event.evenementSousCategorieId),
                        )
                    }.flatten(),
            )
        }
    }

    private fun buildSousCategorieEvenementDetails(
        userInfo: WrappedUserInfo,
        sousCategorie: EvenementSousCategorieWithComplementData,
    ): Collection<EvenementSousCategorieDetails> {
        return if (sousCategorie.evenementSousCategorieComplement.isEmpty()) {
            // Si aucun paramètre n'est défini, retourne la collection avec sousTypeEvenementParametre = null
            listOf(
                EvenementSousCategorieDetails(
                    evenementSousCategorieId = sousCategorie.evenementSousCategorieId,
                    evenementSousCategorieCode = sousCategorie.evenementSousCategorieCode,
                    evenementSousCategorieLibelle = sousCategorie.evenementSousCategorieLibelle,
                    evenementSousCategorieTypeGeometrie = sousCategorie.evenementSousCategorieTypeGeometrie,
                    evenementSousCategorieComplement = null, // Aucun paramètre, donc null
                ),
            )
        } else {
            // Si une sous-catégorie d'évènement existe, on exécute normalement
            generateTypeEvent(sousCategorie).map { typeEvent ->
                buildDynamicForm.executeForEvenementComplement(userInfo, typeEvent).let {
                    EvenementSousCategorieDetails(
                        evenementSousCategorieId = it.dynamicFormId,
                        evenementSousCategorieCode = sousCategorie.evenementSousCategorieCode,
                        evenementSousCategorieLibelle = it.dynamicFormLibelle,
                        evenementSousCategorieTypeGeometrie = sousCategorie.evenementSousCategorieTypeGeometrie,
                        evenementSousCategorieComplement = it.listeParametre,
                    )
                }
            }
        }
    }

    private fun generateTypeEvent(category: EvenementSousCategorieWithComplementData): List<TypeEvent> {
        return category.evenementSousCategorieComplement.map { eventParameter ->
            TypeEvent(
                id = category.evenementSousCategorieId,
                libelle = category.evenementSousCategorieLibelle,
                parameters = listOf(eventParameter),
            )
        }
    }

    /**
     * Exécute la requête pour récupérer les toponymies en fonction des types sélectionnés
     */
    fun getToponymies(criseId: UUID, libelle: String): List<ToponymieResult> {
        // récupérer les informations de la crise pour filtrer les évènements
        val crise = criseRepository.getCrise(criseId)

        // récupère les types sélectionnés par l'utilisateur (protégés / non protégés)
        val nonProteges = criseRepository.getSelectedTypes(crise.listeToponymieId, false)
        val proteges = criseRepository.getSelectedTypes(crise.listeToponymieId, true)
        val results = mutableListOf<ToponymieResult>()
        val globalGeometry = criseRepository.getCriseGeometryUnion(criseId)

        // Requête pour les toponymies non protégées
        if (globalGeometry != null && nonProteges.isNotEmpty()) {
            results.addAll(
                criseRepository.getToponymiesNonProtegesQuery(nonProteges, globalGeometry.toGeomFromText(appSettings.epsg.name), libelle),
            )
        }

        // Requête pour les toponymies protégées
        if (globalGeometry != null && proteges.isNotEmpty()) {
            results.addAll(
                criseRepository.getToponymiesProtegesQuery(proteges, globalGeometry.toGeomFromText(appSettings.epsg.name), libelle),
            )
        }

        return results
    }
}
