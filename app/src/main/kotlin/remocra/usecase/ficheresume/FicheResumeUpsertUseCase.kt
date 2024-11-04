package remocra.usecase.ficheresume

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.FicheResumeBlocData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.FicheResumeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.FicheResumeBloc
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class FicheResumeUpsertUseCase @Inject constructor(private val ficheResumeRepository: FicheResumeRepository) :
    AbstractCUDUseCase<Collection<FicheResumeBlocData>>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_FICHE_RESUME_FORBIDDEN)
        }
    }

    override fun postEvent(element: Collection<FicheResumeBlocData>, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                // Ici on n'a pas d'id, la fiche résumé étant commune à toute l'appli
                pojoId = UUID.randomUUID(),
                typeOperation = typeOperation,
                typeObjet = TypeObjet.FICHE_RESUME,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(
        userInfo: UserInfo?,
        element: Collection<FicheResumeBlocData>,
    ): Collection<FicheResumeBlocData> {
        ficheResumeRepository.deleteFicheResumeBloc()
        element.forEach {
            ficheResumeRepository.upsertFicheResume(
                FicheResumeBloc(
                    ficheResumeBlocId = it.ficheResumeBlocId ?: UUID.randomUUID(),
                    ficheResumeBlocTypeResumeData = it.ficheResumeBlocTypeResumeData,
                    ficheResumeBlocTitre = it.ficheResumeBlocTitre,
                    ficheResumeBlocColonne = it.ficheResumeBlocColonne,
                    ficheResumeBlocLigne = it.ficheResumeBlocLigne,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: Collection<FicheResumeBlocData>) {
        // pas de contraintes
    }
}
