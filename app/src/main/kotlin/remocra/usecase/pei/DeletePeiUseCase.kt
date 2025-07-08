package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PeiData
import remocra.data.enums.ErrorType
import remocra.db.AireAspirationRepository
import remocra.db.AnomalieRepository
import remocra.db.DebitSimultaneRepository
import remocra.db.DocumentRepository
import remocra.db.IndisponibiliteTemporaireRepository
import remocra.db.TourneeRepository
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.pojos.Tournee
import remocra.exception.RemocraResponseException
import remocra.usecase.document.UpsertDocumentPeiUseCase
import remocra.usecase.indisponibilitetemporaire.DeleteIndisponibiliteTemporaireUseCase
import remocra.usecase.indisponibilitetemporaire.UpdateIndisponibiliteTemporaireUseCase
import remocra.usecase.visites.DeleteVisiteUseCase
import java.util.UUID

class DeletePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.DELETE) {

    @Inject
    lateinit var indisponibiliteTemporaireUseCase: IndisponibiliteTemporaireRepository

    @Inject
    lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    lateinit var deleIndisponibiliteTemporaireUseCase: DeleteIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var updateIndisponibiliteTemporaireUseCase: UpdateIndisponibiliteTemporaireUseCase

    @Inject
    lateinit var tourneeRepository: TourneeRepository

    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    @Inject
    lateinit var aireAspirationRepository: AireAspirationRepository

    @Inject
    lateinit var upsertDocument: UpsertDocumentPeiUseCase

    @Inject
    lateinit var documentRepository: DocumentRepository

    @Inject
    lateinit var deleteVisiteUseCase: DeleteVisiteUseCase

    override fun executeSpecific(userInfo: WrappedUserInfo, element: PeiData) {
        // Gestion des Indisponibilités temporaires
        val listeIndisponibiliteTemporaire = indisponibiliteTemporaireUseCase.getWithListPeiByPei(element.peiId)
        listeIndisponibiliteTemporaire.forEach { indisponibiliteTemporaire ->

            /* Si un seul PEI dans l'indisponibilité temporaire un supprimer l'IT
               sinon on la modifie */
            if (indisponibiliteTemporaire.indisponibiliteTemporaireListePeiId.size == 1) {
                if (indisponibiliteTemporaire.indisponibiliteTemporaireDateDebut.isBefore(dateUtils.now()) &&
                    indisponibiliteTemporaire.indisponibiliteTemporaireDateFin?.isAfter(dateUtils.now()) != false
                ) {
                    throw RemocraResponseException(ErrorType.PEI_INDISPONIBILITE_TEMPORAIRE_EN_COURS)
                }
                deleIndisponibiliteTemporaireUseCase.execute(userInfo, indisponibiliteTemporaire, transactionManager)
            } else {
                updateIndisponibiliteTemporaireUseCase.execute(
                    userInfo = userInfo,
                    // "element" est une copie de l'indisponibilité temporaire avec le PEI en cours de suppression en moins
                    element = indisponibiliteTemporaire.copy(
                        indisponibiliteTemporaireListePeiId = indisponibiliteTemporaire.indisponibiliteTemporaireListePeiId
                            .minus(element.peiId),
                    ),
                    transactionManager,
                )
            }
        }

        // Gestion des tournées
        val listeTournee = tourneeRepository.getTourneeByPei(element.peiId)
        listeTournee.forEach { tournee: Tournee ->
            // impossible de modifier une tournée réservée
            if (tournee.tourneeReservationUtilisateurId != null) {
                throw RemocraResponseException(ErrorType.PEI_TOURNEE_LECTURE_SEULE)
            }
            tourneeRepository.deleteLTourneePeiByTourneeAndPeiId(tourneeId = tournee.tourneeId, peiId = element.peiId)
        }

        // Suppression des Aires d'aspiration
        if (element.peiTypePei == TypePei.PENA) {
            aireAspirationRepository.deleteAireAspiration(element.peiId)
        }

        // Suppression des documents
        val listeDocsToRemove = mutableListOf<UUID>()
        documentRepository.getDocumentByPei(element.peiId).forEach {
            listeDocsToRemove.add(it.documentId)
        }

        upsertDocument.deleteLDocument(listeDocsToRemove, transactionManager)

        // Suppression des visites
        visiteRepository.getAllVisiteByIdPei(element.peiId).forEach {
            deleteVisiteUseCase.execute(userInfo, it, transactionManager)
        }

        // Suppression des liaisons anomalies
        anomalieRepository.deleteLiaisonByPei(element.peiId)

        // Suppression dans la table PIBI ou PENA
        when (element.peiTypePei) {
            TypePei.PENA -> {
                penaRepository.deleteLienPenaTypeEngin(element.peiId)
                penaRepository.deleteById(element.peiId)
            }

            TypePei.PIBI -> {
                pibiRepository.deleteById(element.peiId)
            }
        }
        // Suppression du PEI
        peiRepository.deleteById(element.peiId)
    }

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.PEI_D)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_D)
        }
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: PeiData) {
        super.checkContraintes(userInfo, element)

        if (!userInfo.hasDroit(droitWeb = Droit.INDISPO_TEMP_D)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_D_INDISPONIBILITE_TEMPORAIRE)
        }
        if (!userInfo.hasDroit(droitWeb = Droit.TOURNEE_A)) {
            throw RemocraResponseException(ErrorType.PEI_FORBIDDEN_D_TOURNEE)
        }

        // Si le PEI a un débit simultané, on n'autorise pas sa suppression
        if (debitSimultaneRepository.existDebitSimultaneWithPibi(element.peiId) && element.peiTypePei == TypePei.PIBI) {
            throw RemocraResponseException(ErrorType.PEI_DELETE_DEBIT_SIMULTANE)
        }
    }
}
