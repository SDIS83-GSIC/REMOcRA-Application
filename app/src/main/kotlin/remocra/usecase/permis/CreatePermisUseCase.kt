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
import remocra.db.jooq.remocra.tables.pojos.LPermisCadastreParcelle
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.document.documenthabilitable.UpsertDocumentPermisUseCase

class CreatePermisUseCase : AbstractCUDGeometrieUseCase<PermisData>(TypeOperation.INSERT) {

    @Inject lateinit var permisRepository: PermisRepository

    @Inject lateinit var upsertDocumentPermisUseCase: UpsertDocumentPermisUseCase

    override fun getListGeometrie(element: PermisData): Collection<Geometry> {
        return listOf(element.permis.permisGeometrie)
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PERMIS_A)) {
            throw RemocraResponseException(ErrorType.PERMIS_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: PermisData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    permisDocuments = null,
                ),
                pojoId = element.permis.permisId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PERMIS,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: PermisData): PermisData {
        // Insertion du permis
        permisRepository.insertPermis(element.permis)
        // Insertion des liens Permis Parcelle
        element.permisCadastreParcelle.let {
                parcelles ->
            val permisParcelleToInsert = parcelles.map { parcelleId ->
                LPermisCadastreParcelle(element.permis.permisId, parcelleId)
            }
            permisRepository.batchInsertPermisParcelle(permisParcelleToInsert)
        }

        if (element.permisDocuments != null) {
            upsertDocumentPermisUseCase.execute(
                userInfo = userInfo,
                element = element.permisDocuments,
                transactionManager,
            )
        }
        return element.copy(permisDocuments = null)
    }

    override fun checkContraintes(userInfo: UserInfo?, element: PermisData) {
        // Pas de contrainte
    }
}
