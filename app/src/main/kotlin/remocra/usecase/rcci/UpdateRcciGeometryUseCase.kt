package remocra.usecase.rcci

import com.google.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.RcciGeometryForm
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.RcciRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase

class UpdateRcciGeometryUseCase : AbstractCUDGeometrieUseCase<RcciGeometryForm>(TypeOperation.UPDATE) {

    @Inject lateinit var rcciRepository: RcciRepository

    override fun getListGeometrie(element: RcciGeometryForm): Collection<Geometry> {
        return listOf(element.rcciGeometrie)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_GEOMETRY_UPDATE_FORBIDDEN)
        }
    }

    override fun postEvent(element: RcciGeometryForm, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rcciId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RCCI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: RcciGeometryForm): RcciGeometryForm {
        rcciRepository.updateGeometry(element.rcciId, element.rcciGeometrie)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: RcciGeometryForm) {
        // no-op
    }
}
