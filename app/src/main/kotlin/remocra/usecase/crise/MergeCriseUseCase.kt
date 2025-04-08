package remocra.usecase.crise

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CriseRepository
import remocra.db.EvenementRepository
import remocra.db.ToponymieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeCriseStatut
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.web.crise.CriseEndpoint.CriseDataMerge
import java.util.UUID

class MergeCriseUseCase : AbstractCUDUseCase<CriseDataMerge>(TypeOperation.UPDATE) {

    @Inject lateinit var criseRepository: CriseRepository

    @Inject lateinit var toponymieRepository: ToponymieRepository

    @Inject lateinit var evenementRepository: EvenementRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_U)) {
            throw RemocraResponseException(ErrorType.CRISE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: CriseDataMerge, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.criseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.CRISE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    /**
     * **Fusionne les crises en une seule crise en concaténant leurs informations
     * (libellé, description, communes, toponymies) et met à jour les données
     * correspondantes dans les repositories.**
     *
     * @param userInfo Informations relatives à l'auteur.
     * @param element Élément contenant les informations pour fusionner les crises.
     *                Il doit inclure l'ID de la crise principale, une liste d'IDs des crises
     *                à fusionner avec la crise principale, ainsi qu'une date de fusion.
     * @return Élément contenant les informations de fusion des crises.
     */
    override fun execute(userInfo: UserInfo?, element: CriseDataMerge): CriseDataMerge {
        // on récupère la crise dans laquelle on va fusionner les données :
        val crise1 = criseRepository.getCrise(element.criseId)
        // on récupère les crises dont les informations sont à déplacer :
        var criseLibelle: String? = crise1.criseLibelle
        var criseDescription: String = crise1.criseDescription ?: ""
        val criseListeCommune: MutableList<UUID>? = crise1.listeCommuneId?.toMutableList()
        val criseListeToponymie: MutableList<UUID>? = crise1.listeToponymieId?.toMutableList()

        element.listeCriseId?.forEach { uuid ->
            val criseX = criseRepository.getCrise(uuid)
            criseLibelle += " " + criseX.criseLibelle
            if (!criseX.criseDescription.isNullOrEmpty()) {
                criseDescription += " " + criseX.criseDescription
            }
            // Ajout des communes et toponymies
            criseListeCommune?.addAll(criseX.listeCommuneId ?: emptyList())
            criseListeToponymie?.addAll(criseX.listeToponymieId ?: emptyList())
            // mise à jour de la seconde crise
            criseRepository.updateCrise(uuid, criseX.criseLibelle, criseX.criseDescription, criseX.criseDateDebut, element.criseDateFin, criseX.typeCriseId, TypeCriseStatut.FUSIONNEE)

            // récupérer les évènements associés à la criseX pour les mettres dans la crise1
            val listEventX = evenementRepository.getEventIdByCriseId(uuid)
            listEventX.forEach { eventId ->
                val eventX = evenementRepository.getEvenement(eventId).copy(evenementCriseId = crise1.criseId)
                evenementRepository.updateEvenement(eventX)
            }
        }

        // mise à jour de la crise de fusion
        criseRepository.updateCrise(
            element.criseId,
            criseLibelle,
            criseDescription,
            crise1.criseDateDebut,
            crise1.criseDateFin,
            crise1.typeCriseId,
            crise1.criseStatutType,
        )
        // => dans les communes
        criseRepository.deleleteLCriseCommune(element.criseId)
        criseRepository.insertLCriseCommune(
            element.criseId,
            criseListeCommune,
        )
        // => dans les toponymies
        criseRepository.deleteLToponymieCrise(element.criseId)
        toponymieRepository.insertLToponymieCrise(
            criseListeToponymie,
            element.criseId,
        )

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: CriseDataMerge) {
    }
}
