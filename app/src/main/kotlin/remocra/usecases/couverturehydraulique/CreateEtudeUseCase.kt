package remocra.usecases.couverturehydraulique

import com.google.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.EtudeData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.CouvertureHydrauliqueRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecases.AbstractCUDUseCase
import remocra.usecases.document.UpsertDocumentEtudeUseCase
import java.time.ZonedDateTime

class CreateEtudeUseCase : AbstractCUDUseCase<EtudeData>(TypeOperation.INSERT) {

    @Inject lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    @Inject lateinit var upsertDocumentEtudeUseCase: UpsertDocumentEtudeUseCase

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ETUDE_C)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: EtudeData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element.copy(
                    listeDocument = null,
                ),
                pojoId = element.etudeId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ETUDE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: EtudeData): EtudeData {
        couvertureHydrauliqueRepository.insertEtude(
            etudeId = element.etudeId,
            typeEtudeId = element.typeEtudeId,
            etudeNumero = element.etudeNumero,
            etudeLibelle = element.etudeLibelle,
            etudeDescription = element.etudeDescription,
            etudeOrganismeId = userInfo!!.organismeId!!,
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

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: EtudeData) {
        if (couvertureHydrauliqueRepository.checkNumeroExists(element.etudeNumero)) {
            throw RemocraResponseException(ErrorType.ETUDE_NUMERO_UNIQUE)
        }
    }
}
