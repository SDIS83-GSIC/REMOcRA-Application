package remocra.usecase.oldeb

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.oldeb.OldebForm
import remocra.data.oldeb.OldebFormInput
import remocra.data.oldeb.OldebLocataireForm
import remocra.data.oldeb.OldebProprieteForm
import remocra.data.oldeb.OldebVisiteDocument
import remocra.data.oldeb.OldebVisiteForm
import remocra.data.oldeb.OldebVisiteSuiteForm
import remocra.db.OldebRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.web.documents.DocumentEndPoint
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

class SelectOldebUseCase @Inject constructor(
    private val oldebRepository: OldebRepository,
) : AbstractUseCase() {
    fun execute(userInfo: WrappedUserInfo, oldebId: UUID): Result {
        if (!userInfo.hasDroit(droitWeb = Droit.OLDEB_U)) {
            throw RemocraResponseException(ErrorType.OLDEB_FORBIDDEN)
        }

        val oldeb = oldebRepository.selectOldeb(oldebId).let {
            OldebForm(
                oldebId = it.oldebId,
                oldebGeometrie = it.oldebGeometrie,
                oldebCommuneId = it.oldebCommuneId,
                oldebCadastreSectionId = it.oldebCadastreSectionId,
                oldebCadastreParcelleId = it.oldebCadastreParcelleId,
                oldebOldebTypeAccesId = it.oldebOldebTypeAccesId,
                oldebOldebTypeZoneUrbanismeId = it.oldebOldebTypeZoneUrbanismeId,
                oldebNumVoie = it.oldebNumVoie,
                oldebVoieId = it.oldebVoieId,
                oldebLieuDitId = it.oldebLieuDitId,
                oldebVolume = it.oldebVolume,
                oldebLargeurAcces = it.oldebLargeurAcces,
                oldebPortailElectrique = it.oldebPortailElectrique,
                oldebCodePortail = it.oldebCodePortail,
                oldebActif = it.oldebActif,
                caracteristiqueList = oldebRepository.selectCaracteristique(oldebId),
            )
        }

        val propriete = oldebRepository.selectPropriete(oldebId)?.let {
            OldebProprieteForm(
                oldebProprieteOldebProprietaireId = it.oldebProprieteOldebProprietaireId,
                oldebProprieteOldebTypeResidenceId = it.oldebProprieteOldebTypeResidenceId,
            )
        }

        val locataire = oldebRepository.selectLocataire(oldebId)?.let {
            OldebLocataireForm(
                oldebLocataireId = it.oldebLocataireId,
                oldebLocataireOrganisme = it.oldebLocataireOrganisme,
                oldebLocataireRaisonSociale = it.oldebLocataireRaisonSociale,
                oldebLocataireCivilite = it.oldebLocataireCivilite,
                oldebLocataireNom = it.oldebLocataireNom,
                oldebLocatairePrenom = it.oldebLocatairePrenom,
                oldebLocataireTelephone = it.oldebLocataireTelephone,
                oldebLocataireEmail = it.oldebLocataireEmail,
            )
        }

        val visiteList = oldebRepository.selectVisite(oldebId).map {
            OldebVisiteForm(
                oldebVisiteId = it.oldebVisiteId,
                oldebVisiteCode = it.oldebVisiteCode,
                oldebVisiteDateVisite = it.oldebVisiteDateVisite,
                oldebVisiteAgent = it.oldebVisiteAgent,
                oldebVisiteObservation = it.oldebVisiteObservation,
                oldebVisiteUtilisateur = null, // ???
                oldebVisiteDebroussaillementParcelleId = it.oldebVisiteDebroussaillementParcelleId,
                oldebVisiteDebroussaillementAccesId = it.oldebVisiteDebroussaillementAccesId,
                oldebVisiteOldebTypeAvisId = it.oldebVisiteOldebTypeAvisId,
                oldebVisiteOldebTypeActionId = it.oldebVisiteOldebTypeActionId,

                anomalieList = oldebRepository.selectVisiteAnomalie(it.oldebVisiteId),
                suiteList = oldebRepository.selectVisiteSuite(it.oldebVisiteId).map {
                        suite ->
                    OldebVisiteSuiteForm(
                        oldebVisiteSuiteId = suite.oldebVisiteSuiteId,
                        oldebVisiteSuiteOldebTypeSuiteId = suite.oldebVisiteSuiteOldebTypeSuiteId,
                        oldebVisiteSuiteDate = suite.oldebVisiteSuiteDate,
                        oldebVisiteSuiteObservation = suite.oldebVisiteSuiteObservation,
                    )
                },
                documentList = oldebRepository.selectVisiteDocument(it.oldebVisiteId).map {
                        document ->
                    OldebVisiteDocument(
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
            OldebFormInput(
                oldeb = oldeb,
                propriete = propriete,
                locataire = locataire,
                visiteList = visiteList,
            ),
        )
    }
}
