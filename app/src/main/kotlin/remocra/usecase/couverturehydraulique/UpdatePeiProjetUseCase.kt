package remocra.usecase.couverturehydraulique

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.PeiProjetData
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.couverturehydraulique.enums.TypePeiProjet
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase

class UpdatePeiProjetUseCase : AbstractCUDGeometrieUseCase<PeiProjetData>(TypeOperation.UPDATE) {
    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: PeiProjetData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiProjetId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI_PROJET,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: PeiProjetData): PeiProjetData {
        couvertureHydrauliqueRepository.updatePeiProjet(
            peiProjetId = element.peiProjetId,
            peiTypePeiProjet = element.peiProjetTypePeiProjet,
            diametreCanalisation = element.peiProjetDiametreCanalisation,
            debit = element.peiProjetDebit,
            capacite = element.peiProjetCapacite,
            geometrie = element.peiProjetGeometrie,
            natureDeciId = element.peiProjetNatureDeciId,
            diametreId = element.peiProjetDiametreId,
        )

        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiProjetData) {
        when (element.peiProjetTypePeiProjet) {
            TypePeiProjet.PA -> {
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
            }
            TypePeiProjet.PIBI -> {
                if (element.peiProjetDiametreId == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_MANQUANT)
                }
                if (element.peiProjetDiametreCanalisation == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DIAMETRE_CANALISATION_MANQUANT)
                }
            }
            TypePeiProjet.RESERVE -> {
                if (element.peiProjetCapacite == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_CAPACITE_MANQUANTE)
                }
                if (element.peiProjetDebit == null) {
                    throw RemocraResponseException(ErrorType.ETUDE_DEBIT_MANQUANT)
                }
            }
        }
    }

    override fun getListGeometrie(element: PeiProjetData): Collection<Geometry> {
        return listOf(element.peiProjetGeometrie)
    }

    override fun ensureSrid(element: PeiProjetData): PeiProjetData {
        return element.copy(
            peiProjetGeometrie = transform(element.peiProjetGeometrie),
        )
    }
}
