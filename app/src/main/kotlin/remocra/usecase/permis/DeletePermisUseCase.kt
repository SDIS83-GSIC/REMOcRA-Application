package remocra.usecase.permis

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PermisData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.PermisRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase

class DeletePermisUseCase : AbstractCUDGeometrieUseCase<PermisData>(TypeOperation.DELETE) {

    @Inject lateinit var permisRepository: PermisRepository

    override fun getListGeometrie(element: PermisData): Collection<Geometry> =
        listOf(element.permis.permisGeometrie)

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: PermisData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.permis.permisId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PERMIS,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: PermisData): PermisData {
        // Suppression liens Permis Parcelle
        permisRepository.deletePermisParcelle(element.permis.permisId)
        // Suppression du permis
        permisRepository.deletePermis(element.permis.permisId)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: PermisData) {
        // Pas de contrainte
    }
}
