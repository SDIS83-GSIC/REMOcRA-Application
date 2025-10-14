package remocra.usecase.rcci

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.RcciGeometryForm
import remocra.data.enums.ErrorType
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

    override fun ensureSrid(element: RcciGeometryForm): RcciGeometryForm {
        if (element.rcciGeometrie.srid != appSettings.srid) {
            return element.copy(
                rcciGeometrie = transform(element.rcciGeometrie),
            )
        }
        return element
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_GEOMETRY_UPDATE_FORBIDDEN)
        }
    }

    override fun postEvent(element: RcciGeometryForm, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.rcciId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.RCCI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: RcciGeometryForm): RcciGeometryForm {
        rcciRepository.updateGeometry(element.rcciId, element.rcciGeometrie)

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: RcciGeometryForm) {
        // no-op
    }
}
