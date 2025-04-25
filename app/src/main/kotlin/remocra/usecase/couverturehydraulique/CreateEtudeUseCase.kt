package remocra.usecase.couverturehydraulique

import com.google.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.EtudeData
import remocra.data.enums.ErrorType
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.UpsertDocumentEtudeUseCase

class CreateEtudeUseCase : AbstractCUDUseCase<EtudeData>(TypeOperation.INSERT) {

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    @Inject lateinit var upsertDocumentEtudeUseCase: UpsertDocumentEtudeUseCase

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ETUDE_C)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: EtudeData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    listeDocument = null,
                ),
                pojoId = element.etudeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ETUDE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: EtudeData): EtudeData {
        couvertureHydrauliqueRepository.insertEtude(
            etudeId = element.etudeId,
            typeEtudeId = element.typeEtudeId,
            etudeNumero = element.etudeNumero,
            etudeLibelle = element.etudeLibelle,
            etudeDescription = element.etudeDescription,
            etudeOrganismeId = userInfo.organismeId,
        )

        // On remplit les L_ETUDE_COMMUNE
        couvertureHydrauliqueRepository.insertLEtudeCommune(
            element.etudeId,
            element.listeCommuneId ?: listOf(),
        )

        if (element.listeDocument != null) {
            // Puis les documents
            upsertDocumentEtudeUseCase.execute(
                userInfo,
                element.listeDocument,
                transactionManager,
            )
        }

        return element.copy(listeDocument = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: EtudeData) {
        if (couvertureHydrauliqueRepository.checkNumeroExists(element.etudeNumero)) {
            throw RemocraResponseException(ErrorType.ETUDE_NUMERO_UNIQUE)
        }
    }
}
