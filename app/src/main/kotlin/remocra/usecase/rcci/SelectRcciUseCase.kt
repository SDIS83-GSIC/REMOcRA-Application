package remocra.usecase.rcci

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.WrappedUserInfo
import remocra.data.RcciDocument
import remocra.data.RcciForm
import remocra.data.RcciFormInput
import remocra.data.enums.ErrorType
import remocra.db.RcciRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.web.documents.DocumentEndPoint
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

class SelectRcciUseCase @Inject constructor(private val rcciRepository: RcciRepository) : AbstractUseCase() {
    fun execute(userInfo: WrappedUserInfo, rcciId: UUID): Result {
        if (!userInfo.hasDroit(droitWeb = Droit.RCCI_R) || !userInfo.hasDroit(droitWeb = Droit.RCCI_A)) {
            throw RemocraResponseException(ErrorType.RCCI_FORBIDDEN)
        }

        val rcci = rcciRepository.selectRcci(rcciId).let {
            RcciForm(
                rcciId = it.rcciId,
                rcciCommentaireConclusion = it.rcciCommentaireConclusion,
                rcciComplement = it.rcciComplement,
                rcciCarroyageDfci = it.rcciCarroyageDfci,
                rcciDateIncendie = it.rcciDateIncendie,
                rcciDateModification = dateUtils.now(),
                rcciDirectionVent = it.rcciDirectionVent,
                rcciForceVent = it.rcciForceVent,
                rcciForcesOrdre = it.rcciForcesOrdre,
                rcciGdh = it.rcciGdh,
                rcciGelLieux = TODO(),
                rcciGeometrie = it.rcciGeometrie,
                rcciHygrometrie = it.rcciHygrometrie,
                rcciIndiceRothermel = TODO(),
                rcciPointEclosion = it.rcciPointEclosion,
                rcciPremierCos = it.rcciPremierCos,
                rcciPremierEngin = it.rcciPremierEngin,
                rcciSuperficieFinale = it.rcciSuperficieFinale,
                rcciSuperficieReferent = it.rcciSuperficieReferent,
                rcciSuperficieSecours = it.rcciSuperficieSecours,
                rcciTemperature = it.rcciTemperature,
                rcciVentLocal = TODO(),
                rcciVoie = it.rcciVoieTexte, // TODO gestion des voies
                rcciCommuneId = it.rcciCommuneId,
                rcciRcciTypePrometheeCategorieId = it.rcciRcciTypePrometheeCategorieId,
                rcciRcciTypeDegreCertitudeId = it.rcciRcciTypeDegreCertitudeId,
                rcciRcciTypeOrigineAlerteId = it.rcciRcciTypeOrigineAlerteId,
                rcciRcciArriveeDdtmOnfId = it.rcciRcciArriveeDdtmOnfId,
                rcciRcciArriveeSdisId = it.rcciRcciArriveeSdisId,
                rcciRcciArriveeGendarmerieId = it.rcciRcciArriveeGendarmerieId,
                rcciRcciArriveePoliceId = it.rcciRcciArriveePoliceId,
                rcciUtilisateurId = it.rcciUtilisateurId,
                documentList = rcciRepository.selectDocument(it.rcciId).map {
                        document ->
                    RcciDocument(
                        documentId = document.documentId,
                        documentNom = document.documentNomFichier,
                        documentUrl = UriBuilder.fromPath(AuthnConstants.API_PATH)
                            .path(DocumentEndPoint::class.java)
                            .path(DocumentEndPoint::telechargerRessource.javaMethod)
                            .build(document.documentId)
                            .toString(),
                    )
                },
            )
        }

        return Result.Success(
            RcciFormInput(
                rcci = rcci,
            ),
        )
    }
}
