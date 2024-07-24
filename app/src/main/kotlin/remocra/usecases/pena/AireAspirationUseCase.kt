package remocra.usecases.pena

import com.google.inject.Inject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import remocra.authn.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.TypeSourceModification
import remocra.db.AireAspirationRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.tables.pojos.PenaAspiration
import remocra.eventbus.EventBus
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecases.AbstractCUDUseCase
import remocra.web.pei.PenaEndPoint
import java.time.Clock
import java.time.ZonedDateTime
import java.util.UUID

class AireAspirationUseCase : AbstractCUDUseCase<AireAspirationUseCase.PenaAspirationData>() {
    @Inject lateinit var aireAspirationRepository: AireAspirationRepository

    @Inject lateinit var eventBus: EventBus

    @Inject lateinit var clock: Clock

    data class PenaAspirationData(
        val listeAireAspiration: List<PenaEndPoint.AireAspirationUpsert>,
        val penaId: UUID,
    )

    override fun checkDroits(userInfo: UserInfo) {
        // TODO
    }

    override fun postEvent(element: PenaAspirationData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.penaId,
                typeOperation = TypeOperation.UPDATE,
                typeObjet = TypeObjet.PENA_ASPIRATION,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.idUtilisateur, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(element: PenaAspirationData): PenaAspirationData {
        // On supprime en amont les aires existantes pour les recréer
        aireAspirationRepository.deleteAireAspiration(element.penaId)

        element.listeAireAspiration.forEach {
            aireAspirationRepository.upsertAireAspiration(
                PenaAspiration(
                    penaAspirationId = it.penaAspirationId ?: UUID.randomUUID(),
                    penaAspirationTypePenaAspirationId = it.typePenaAspirationId,
                    penaAspirationNumero = it.numero,
                    penaAspirationPenaId = element.penaId,
                    // TODO SRID
                    penaAspirationGeometrie = if (!it.coordonneeX.isNullOrEmpty() && !it.coordonneeY.isNullOrEmpty() && it.estDeporte) {
                        GeometryFactory(PrecisionModel(), 2154).createPoint(Coordinate(it.coordonneeX.toDouble(), it.coordonneeY.toDouble()))
                    } else {
                        null
                    },
                    penaAspirationEstDeporte = it.estDeporte,
                    penaAspirationEstNormalise = it.estNormalise,
                    penaAspirationHauteurSuperieure_3Metres = it.hauteurSuperieure3Metres,
                ),
            )
        }

        return element
    }

    override fun checkContraintes(element: PenaAspirationData) {
        // Vérifie si on n'a pas de doublon sur le numéro
        if (element.listeAireAspiration.groupingBy { it.numero }.eachCount().any { it.value > 1 }) {
            throw IllegalArgumentException("Le numéro doit être unique")
        }

        if (element.listeAireAspiration.filter { it.estDeporte && (it.coordonneeX == null || it.coordonneeY == null) }.isNotEmpty()) {
            throw IllegalArgumentException("Si une aire d'aspiration est déportée, les coordonnées doivent être renseignées.")
        }

        // TODO vérifier la géométrie (bien dans la zone de compétence de l'utilisateur connecté)
    }
}
