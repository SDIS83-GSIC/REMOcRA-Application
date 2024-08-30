package remocra.usecases.indisponibiliteTemporaire

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.IndisponibiliteTemporaireData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.IndisponibiliteTemporaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecases.AbstractCUDUseCase
import java.time.ZonedDateTime

class CloreIndisponibiliteTemporaireUseCase
@Inject constructor(
    private val indisponibiliteTemporaireRepository: IndisponibiliteTemporaireRepository,
) : AbstractCUDUseCase<IndisponibiliteTemporaireData>(TypeOperation.UPDATE) {
    override fun postEvent(element: IndisponibiliteTemporaireData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.indisponibiliteTemporaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.INDISPONIBILITE_TEMPORAIRE,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = ZonedDateTime.now(clock),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: IndisponibiliteTemporaireData): IndisponibiliteTemporaireData {
        val indisponibiliteTemporaire = IndisponibiliteTemporaire(
            indisponibiliteTemporaireId = element.indisponibiliteTemporaireId,
            indisponibiliteTemporaireMotif = element.indisponibiliteTemporaireMotif,
            indisponibiliteTemporaireObservation = element.indisponibiliteTemporaireObservation,
            indisponibiliteTemporaireMailApresIndisponibilite = element.indisponibiliteTemporaireMailApresIndisponibilite,
            indisponibiliteTemporaireMailAvantIndisponibilite = element.indisponibiliteTemporaireMailAvantIndisponibilite,
            indisponibiliteTemporaireDateDebut = element.indisponibiliteTemporaireDateDebut,
            indisponibiliteTemporaireDateFin = ZonedDateTime.now(clock), // date actuelle pour "clore" l'indispo
            indisponibiliteTemporaireBasculeAutoDisponible = element.indisponibiliteTemporaireBasculeAutoDisponible,
            indisponibiliteTemporaireBasculeAutoIndisponible = element.indisponibiliteTemporaireBasculeAutoIndisponible,
        )
        indisponibiliteTemporaireRepository.upsert(indisponibiliteTemporaire)

        indisponibiliteTemporaireRepository.deleteLiaisonByIndisponibiliteTemporaire(element.indisponibiliteTemporaireId)
        element.indisponibiliteTemporaireListePeiId.forEach { peiId ->
            indisponibiliteTemporaireRepository.insertLiaisonIndisponibiliteTemporairePei(
                indisponibiliteTemporaireId = indisponibiliteTemporaire.indisponibiliteTemporaireId,
                peiId,
            )
        }

        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: IndisponibiliteTemporaireData) {
        element.indisponibiliteTemporaireDateFin?.let {
            if (element.indisponibiliteTemporaireDateDebut > element.indisponibiliteTemporaireDateFin) {
                throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FIN_AVANT_DEBUT)
            }
        }
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.INDISPO_TEMP_U)) {
            throw RemocraResponseException(ErrorType.INDISPONIBILITE_TEMPORAIRE_FORBIDDEN_UPDATE)
        }
    }
}
