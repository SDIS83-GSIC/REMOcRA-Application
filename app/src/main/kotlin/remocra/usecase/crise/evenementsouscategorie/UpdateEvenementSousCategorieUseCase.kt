package remocra.usecase.crise.evenementsouscategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.enums.ErrorType
import remocra.db.EvenementRepository
import remocra.db.EvenementSousCategorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.CriseEvenementComplement
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.db.jooq.remocra.tables.pojos.LEvenementCriseEvenementComplement
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateEvenementSousCategorieUseCase :
    AbstractCUDUseCase<EvenementSousCategorieWithComplementData>(TypeOperation.UPDATE) {

    @Inject private lateinit var evenementSousCategorieRepository: EvenementSousCategorieRepository

    @Inject private lateinit var evenementRepository: EvenementRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_UPDATE)
        }
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorieWithComplementData,
    ) {
        // Pas de contraintes
    }

    override fun execute(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorieWithComplementData,
    ): EvenementSousCategorieWithComplementData {
        val currentElement = EvenementSousCategorie(
            evenementSousCategorieId = element.evenementSousCategorieId,
            evenementSousCategorieCode = element.evenementSousCategorieCode,
            evenementSousCategorieLibelle = element.evenementSousCategorieLibelle,
            evenementSousCategorieTypeGeometrie = element.evenementSousCategorieTypeGeometrie,
            evenementSousCategorieEvenementCategorieId = element.evenementSousCategorieEvenementCategorieId,
            evenementSousCategorieActif = element.evenementSousCategorieActif,
        )
        evenementSousCategorieRepository.update(currentElement)

        // on récupère tous les compléments déjà présents pour ce sous-type
        val currentComplement = evenementSousCategorieRepository.getById(element.evenementSousCategorieId).evenementSousCategorieComplement
        // Liste des nouveaux compléments à garder/ajouter
        val newComplementIds = element.evenementSousCategorieComplement.map { it.sousCategorieComplementId }
        // On récupère l'id des compléments qui ne sont pas dans la nouvelle liste des compléments. Cela donne les compléments à supprimer.
        currentComplement.map { it.sousCategorieComplementId }.filterNot { newComplementIds.contains(it) }.forEach {
            // on supprime dans l_evenement_crise_evenement_complément, toutes les lignes dont l'id du complément n'existeront plus
            evenementSousCategorieRepository.deleteLEvenementCriseComplementByComplementId(it)
            // on supprime dans crise_evenement_complement, toutes les lignes dont l'id du complément n'existeront plus
            evenementSousCategorieRepository.deleteCriseEvenementComplementByComplementId(it)
        }

        // on met à jour les autres lignes
        element.evenementSousCategorieComplement.forEach { parametre ->
            parametre.let {
                evenementSousCategorieRepository.upsertSousTypeParametre(
                    CriseEvenementComplement(
                        criseEvenementComplementId = parametre.sousCategorieComplementId,
                        criseEvenementComplementLibelle = parametre.sousCategorieComplementLibelle,
                        criseEvenementComplementSourceSql = parametre.sousCategorieComplementSql,
                        criseEvenementComplementSourceSqlId = parametre.sousCategorieComplementSqlId,
                        criseEvenementComplementSourceSqlLibelle = parametre.sousCategorieComplementSqlLibelle,
                        criseEvenementComplementValeurDefaut = parametre.sousCategorieComplementValeurDefaut,
                        criseEvenementComplementEstRequis = parametre.sousCategorieComplementEstRequis,
                        criseEvenementComplementType = parametre.sousCategorieComplementType,
                        criseEvenementComplementEvenementSousCategorieId = element.evenementSousCategorieId,
                    ),
                )
            }
        }

        // Pour chaque évènement, on ajoute le complément si nécessaire avec sa valeur par défaut
        evenementRepository.getEventIdBySubType(element.evenementSousCategorieId).map { it.evenementId }.forEach { evenementId ->
            element.evenementSousCategorieComplement.forEach { parametre ->

                // Ajouter le complément avec la valeur par défaut s'il n'existe pas déjà pour cet évènement
                evenementSousCategorieRepository.upsertEvenementComplement(
                    LEvenementCriseEvenementComplement(
                        evenementId = evenementId,
                        criseEvenementComplementId = parametre.sousCategorieComplementId,
                        valeur = parametre.sousCategorieComplementValeurDefaut ?: "",
                    ),
                )
            }
        }

        return element
    }

    override fun postEvent(
        element: EvenementSousCategorieWithComplementData,
        userInfo: WrappedUserInfo,
    ) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.evenementSousCategorieId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT_SOUS_CATEGORIE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }
}
