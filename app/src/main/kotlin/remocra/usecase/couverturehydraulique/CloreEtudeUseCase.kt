package remocra.usecase.couverturehydraulique

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
import remocra.usecase.AbstractCUDUseCase
import java.time.ZonedDateTime
import java.util.UUID

class CloreEtudeUseCase : AbstractCUDUseCase<UUID>(TypeOperation.UPDATE) {

    @Inject
    lateinit var couvertureHydrauliqueRepository: CouvertureHydrauliqueRepository

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ETUDE_U)) {
            throw RemocraResponseException(ErrorType.ETUDE_TYPE_FORBIDDEN_C)
        }
    }

    override fun postEvent(element: UUID, userInfo: UserInfo) {
        val etude = couvertureHydrauliqueRepository.getEtude(element)
        eventBus.post(
            TracabiliteEvent(
                pojo = EtudeData(
                    etudeId = element,
                    typeEtudeId = etude.typeEtudeId,
                    etudeNumero = etude.etudeNumero,
                    etudeLibelle = etude.etudeLibelle,
                    etudeDescription = etude.etudeDescription,
                    listeCommuneId = etude.listeCommuneId,
                    listeDocument = null,
                ),
                pojoId = element,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ETUDE,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: UUID): UUID {
        couvertureHydrauliqueRepository.cloreEtude(element)

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: UUID) {
        // noop pas de contrainte
    }
}
