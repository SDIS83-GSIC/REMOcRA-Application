package remocra.usecase.peiPrescrit

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.PeiPrescritRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.PeiPrescrit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase

class CreatePeiPrescritUseCase : AbstractCUDGeometrieUseCase<PeiPrescrit>(TypeOperation.INSERT) {

    @Inject lateinit var peiPrescritRepository: PeiPrescritRepository

    override fun getListGeometrie(element: PeiPrescrit): Collection<Geometry> {
        return listOf(element.peiPrescritGeometrie)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.PEI_PRESCRIT_A)) {
            throw RemocraResponseException(ErrorType.PEI_PRESCRIT_FORBIDDEN_INSERT)
        }
    }

    override fun ensureSrid(element: PeiPrescrit): PeiPrescrit {
        if (element.peiPrescritGeometrie.srid != appSettings.srid) {
            return element.copy(
                peiPrescritGeometrie = transform(element.peiPrescritGeometrie),
            )
        }
        return element
    }

    override fun postEvent(element: PeiPrescrit, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiPrescritId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI_PRESCRIT,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: PeiPrescrit): PeiPrescrit {
        peiPrescritRepository.insertPeiPrescrit(element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiPrescrit) {
        // Pas de contrainte
    }
}
