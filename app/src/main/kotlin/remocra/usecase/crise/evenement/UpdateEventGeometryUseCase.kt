package remocra.usecase.crise.evenement

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.EvenementGeometrieData
import remocra.data.MessageData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.EvenementRepository
import remocra.db.MessageRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import java.util.UUID

class UpdateEventGeometryUseCase : AbstractCUDGeometrieUseCase<EvenementGeometrieData>(TypeOperation.UPDATE) {

    @Inject private lateinit var eventRepository: EvenementRepository

    @Inject private lateinit var messageRepository: MessageRepository

    override fun getListGeometrie(element: EvenementGeometrieData): Collection<Geometry> {
        return listOf(element.eventGeometrie)
    }

    override fun ensureSrid(element: EvenementGeometrieData): EvenementGeometrieData {
        if (element.eventGeometrie.srid != appSettings.srid) {
            return element.copy(
                eventGeometrie = transform(element.eventGeometrie),
            )
        }
        return element
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.CRISE_U)) {
            throw RemocraResponseException(ErrorType.EVENEMENT_GEOMETRY_UPDATE_FORBIDDEN)
        }
    }

    override fun postEvent(element: EvenementGeometrieData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.eventId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.EVENEMENT,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: EvenementGeometrieData): EvenementGeometrieData {
        eventRepository.updateGeometry(element.eventId, element.eventGeometrie)

        // - message
        messageRepository.add(
            MessageData(
                messageObjet = "Modification géométrique d’événement",
                messageDescription = "",
                messageDateConstat = dateUtils.now(),
                messageImportance = 0,
                messageOrigine = "",
                messageTags = "Modification géométrique",
                messageId = UUID.randomUUID(),
                messageEvenementId = element.eventId,
                messageUtilisateurId = userInfo!!.utilisateurId,
            ),
        )

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: EvenementGeometrieData) {
        // no-op
    }
}
