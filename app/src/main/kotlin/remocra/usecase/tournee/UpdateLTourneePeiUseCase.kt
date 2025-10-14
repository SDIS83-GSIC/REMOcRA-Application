package remocra.usecase.tournee

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.TourneeRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.LTourneePei
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import java.util.UUID

class UpdateLTourneePeiUseCase @Inject constructor(
    private val tourneeRepository: TourneeRepository,
    private val peiRepository: PeiRepository,
) : AbstractCUDGeometrieUseCase<UpdateLTourneePeiUseCase.LTourneePeiToInsert>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.TOURNEE_A)) {
            throw RemocraResponseException(ErrorType.TOURNEE_GESTION_FORBIDDEN)
        }
    }

    override fun getListGeometrie(element: LTourneePeiToInsert): Collection<Geometry> {
        if (element.listLTourneePei.isNullOrEmpty()) {
            return listOf()
        }
        return peiRepository.getGeometriesPei(element.listLTourneePei.map { it.peiId })
    }

    override fun ensureSrid(element: LTourneePeiToInsert): LTourneePeiToInsert {
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: LTourneePeiToInsert) {
        // On vérifie que tous les pei ont bien la même nature deci
        val codesNatureDeci = peiRepository.getNatureDeciId(element.listLTourneePei?.map { it.peiId }?.toSet() ?: setOf())
        if (codesNatureDeci.distinct().size > 1) {
            throw RemocraResponseException(ErrorType.TOURNEE_NATURE_DECI)
        }
    }

    override fun postEvent(element: LTourneePeiToInsert, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.tourneeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.TOURNEE_PEI,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: LTourneePeiToInsert): LTourneePeiToInsert {
        // Le plus simple ici est de vider les infos relatives à notre tournée pour tout réinsérer au propre
        tourneeRepository.deleteLTourneePeiByTourneeId(tourneeId = element.tourneeId)
        if (!element.listLTourneePei.isNullOrEmpty()) {
            tourneeRepository.batchInsertLTourneePei(listeTourneePei = element.listLTourneePei)
        }
        return element
    }

    data class LTourneePeiToInsert(
        val tourneeId: UUID,
        val listLTourneePei: List<LTourneePei>?,
    )
}
