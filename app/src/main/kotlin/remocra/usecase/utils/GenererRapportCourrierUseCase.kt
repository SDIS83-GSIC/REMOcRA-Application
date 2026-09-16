package remocra.usecase.utils

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.DestinataireData
import remocra.data.courrier.form.CourrierData
import remocra.data.courrier.form.NomValue
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.DocumentRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.usecase.AbstractUseCase
import remocra.usecase.courrier.CourrierGeneratorUseCase
import remocra.usecase.courrier.CreateCourrierUseCase
import java.util.UUID

class GenererRapportCourrierUseCase @Inject constructor(
    private val courrierGeneratorUseCase: CourrierGeneratorUseCase,
    private val createCourrierUseCase: CreateCourrierUseCase,
    private val documentRepository: DocumentRepository,
) : AbstractUseCase() {

    fun execute(
        modeleCourrier: ModeleCourrier,
        courrierReference: String,
        parametres: List<NomValue>,
        destinataires: List<DestinataireData>,
        userInfo: WrappedUserInfo,
        transactionManager: TransactionManager? = null,
        peiId: UUID? = null,
    ): Result {
        val generationPath = courrierGeneratorUseCase.executeInternal(
            ParametreCourrierInput(
                modeleCourrierId = modeleCourrier.modeleCourrierId,
                courrierReference = courrierReference,
                listParametres = parametres,
            ),
            userInfo,
            mainTransactionManager = transactionManager,
        )

        val docId = UUID.randomUUID()
        val courrier = createCourrierUseCase.execute(
            userInfo,
            CourrierData(
                courrierId = UUID.randomUUID(),
                documentId = docId,
                modeleCourrierId = modeleCourrier.modeleCourrierId,
                nomDocumentTmp = generationPath.fileName.toString(),
                listeDestinataire = destinataires,
                courrierReference = courrierReference,
                codeThematique = GlobalConstants.THEMATIQUE_POINT_EAU,
            ),
            mainTransactionManager = transactionManager,
        )

        peiId?.let {
            documentRepository.insertDocumentPei(it, docId, false)
        }

        return courrier
    }
}
