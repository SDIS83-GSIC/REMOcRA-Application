package remocra.usecase.pena

import com.google.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AireAspirationUpsertData
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.AireAspirationRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.PenaAspiration
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import java.util.UUID

class AireAspirationUseCase : AbstractCUDGeometrieUseCase<AireAspirationUseCase.PenaAspirationData>(TypeOperation.UPDATE) {
    @Inject lateinit var aireAspirationRepository: AireAspirationRepository

    data class PenaAspirationData(
        val listeAireAspiration: List<AireAspirationUpsertData>,
        val penaId: UUID,
    )

    override fun ensureSrid(element: PenaAspirationData): PenaAspirationData {
        if (element.listeAireAspiration.any { it.geometrie != null && it.geometrie.srid != appSettings.srid }) {
            return element.copy(
                listeAireAspiration = element.listeAireAspiration.map {
                    it.copy(geometrie = it.geometrie?.let { g -> transform(g) })
                },
            )
        }
        return element
    }

    override fun getListGeometrie(element: PenaAspirationData): Collection<Geometry> {
        val geometries: MutableList<Geometry> = mutableListOf()
        element.listeAireAspiration.map {
            if (it.estDeporte && it.geometrie != null) {
                geometries.add(
                    it.geometrie,
                )
            }
        }

        return geometries
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.PEI_CARACTERISTIQUES_U) ||
            !userInfo.droits.contains(Droit.PEI_U)
        ) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_U)
        }
    }

    override fun postEvent(element: PenaAspirationData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.penaId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PENA_ASPIRATION,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: PenaAspirationData): PenaAspirationData {
        // On supprime en amont les aires existantes pour les recréer
        aireAspirationRepository.deleteAireAspiration(element.penaId)

        element.listeAireAspiration.forEach {
            aireAspirationRepository.upsertAireAspiration(
                PenaAspiration(
                    penaAspirationId = it.penaAspirationId ?: UUID.randomUUID(),
                    penaAspirationTypePenaAspirationId = it.typePenaAspirationId,
                    penaAspirationNumero = it.numero,
                    penaAspirationPenaId = element.penaId,
                    penaAspirationGeometrie = if (it.geometrie != null && it.estDeporte) {
                        it.geometrie
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

    override fun checkContraintes(userInfo: UserInfo?, element: PenaAspirationData) {
        // Vérifie si on n'a pas de doublon sur le numéro
        if (element.listeAireAspiration.groupingBy { it.numero }.eachCount().any { it.value > 1 }) {
            throw IllegalArgumentException("Le numéro doit être unique")
        }

        if (element.listeAireAspiration.filter { it.estDeporte && it.geometrie == null }.isNotEmpty()) {
            throw IllegalArgumentException("Si une aire d'aspiration est déportée, les coordonnées doivent être renseignées.")
        }
    }
}
