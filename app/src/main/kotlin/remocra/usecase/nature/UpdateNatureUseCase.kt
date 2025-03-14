package remocra.usecase.nature

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.NatureWithDiametres
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.data.enums.TypeSourceModification
import remocra.db.NatureRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class UpdateNatureUseCase @Inject constructor(private val natureRepository: NatureRepository) :
    AbstractCUDUseCase<NatureWithDiametres>(TypeOperation.UPDATE) {
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: NatureWithDiametres, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.natureId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.NATURE,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
        // Si la nomenclature modifiée fait partie du DataCache
        // Alors MiseAJour du Cache en question
        eventBus.post(DataCacheModifiedEvent(TypeDataCache.NATURE))
    }

    override fun execute(userInfo: UserInfo?, element: NatureWithDiametres): NatureWithDiametres {
        natureRepository.edit(element)

        natureRepository.deleteLienDiametreNature(element.natureId)
        natureRepository.addLienDiametreNature(element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: NatureWithDiametres) {
        if (element.natureProtected!!) {
            throw RemocraResponseException(ErrorType.ADMIN_DIAMETRE_IS_PROTECTED)
        }
    }
}
