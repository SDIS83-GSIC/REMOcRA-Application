package remocra.usecase.crise.evenementsouscategorie

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EvenementSousCategorieWithComplementData
import remocra.data.enums.ErrorType
import remocra.db.EvenementSousCategorieRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.CriseEvenementComplement
import remocra.db.jooq.remocra.tables.pojos.EvenementSousCategorie
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateEvenementSousCategorieUseCase : AbstractCUDUseCase<EvenementSousCategorieWithComplementData>(TypeOperation.INSERT) {

    @Inject lateinit var evenementSousCategorieRepository: EvenementSousCategorieRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_INSERT)
        }
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

    override fun execute(userInfo: WrappedUserInfo, element: EvenementSousCategorieWithComplementData): EvenementSousCategorieWithComplementData {
        evenementSousCategorieRepository.insert(
            EvenementSousCategorie(
                evenementSousCategorieId = element.evenementSousCategorieId,
                evenementSousCategorieCode = element.evenementSousCategorieCode,
                evenementSousCategorieLibelle = element.evenementSousCategorieLibelle,
                evenementSousCategorieTypeGeometrie = element.evenementSousCategorieTypeGeometrie,
                evenementSousCategorieEvenementCategorieId = element.evenementSousCategorieEvenementCategorieId,
                evenementSousCategorieActif = element.evenementSousCategorieActif,
            ),
        )

        // insérer les paramètres
        element.evenementSousCategorieComplement.forEach { parametre ->
            parametre?.let {
                evenementSousCategorieRepository.upsertSousTypeParametre(
                    CriseEvenementComplement(
                        criseEvenementComplementId = parametre.sousCategorieComplementId,
                        criseEvenementComplementLibelle = parametre.sousCategorieComplementLibelle,
                        criseEvenementComplementSourceSql = parametre.sousCategorieComplementSql,
                        criseEvenementComplementSourceSqlId = parametre.sousCategorieComplementSqlId,
                        criseEvenementComplementSourceSqlLibelle = parametre.sousCategorieComplementSqlLibelle,
                        criseEvenementComplementValeurDefaut = parametre.sousCategorieComplementValeurDefaut,
                        criseEvenementComplementEstRequis = parametre.sousCategorieComplementEstRequis,
                        criseEvenementComplementEvenementSousCategorieId = element.evenementSousCategorieId,
                        criseEvenementComplementType = parametre.sousCategorieComplementType,
                    ),
                )
            }
        }

        return element
    }

    override fun checkContraintes(
        userInfo: WrappedUserInfo,
        element: EvenementSousCategorieWithComplementData,
    ) {
        // Pas de contraintes
    }
}
