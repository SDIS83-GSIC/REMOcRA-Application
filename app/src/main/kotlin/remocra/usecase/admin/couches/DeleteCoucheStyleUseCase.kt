package remocra.usecase.admin.couches

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import java.util.UUID

class DeleteCoucheStyleUseCase : AbstractCUDUseCase<UUID>(TypeOperation.DELETE) {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.CARTO_METADATA_A)) {
            throw RemocraResponseException(ErrorType.GROUPE_FONCTIONNALITES_COUCHE_DELETE)
        }
    }

    override fun postEvent(element: UUID, userInfo: WrappedUserInfo) {
    }

    override fun execute(userInfo: WrappedUserInfo, element: UUID): UUID {
        // supprimer tout id dans L_PROFIL_DROIT_COUCHE
        groupeFonctionnalitesRepository.deleteLGroupeFonctionnalitesCouche(element)

        // supprimer le style
        coucheRepository.deleteCoucheStyleByStyleId(element)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: UUID) {}
}
