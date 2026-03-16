package remocra.apiapachehop.usecase

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.apiapachehop.data.NotifierCourrierData
import remocra.auth.WrappedUserInfo
import remocra.data.courrier.form.CourrierData
import remocra.data.courrier.form.NomValue
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.ModeleCourrierRepository
import remocra.log.LogManagerFactory
import remocra.usecase.AbstractUseCase
import remocra.usecase.courrier.CourrierGeneratorUseCase
import remocra.usecase.courrier.CreateCourrierUseCase
import java.util.UUID

class NotifierCourrierUseCase @Inject constructor(
    private val modeleCourrierRepository: ModeleCourrierRepository,
    private val logManagerFactory: LogManagerFactory,
    private val courrierGeneratorUseCase: CourrierGeneratorUseCase,
    private val createCourrierUseCase: CreateCourrierUseCase,
) : AbstractUseCase() {

    fun execute(notifierCourrierData: NotifierCourrierData, userInfo: WrappedUserInfo) {
        val logManager = logManagerFactory.create(notifierCourrierData.jobId)

        // Première étape : générer le courrier
        // on va chercher le modèle de courrier à partir du code fourni dans le data
        val modeleCourrierId = modeleCourrierRepository.getIdByCode(notifierCourrierData.modeleCourrierCode)

        if (modeleCourrierId == null) {
            logManager.error("Aucun modèle de courrier trouvé pour le code ${notifierCourrierData.modeleCourrierCode}")
            return
        }
        val reference = notifierCourrierData.jobId.toString()

        // pour chaque courrier à générer, on appelle le use case de génération de courrier en lui passant les paramètres nécessaires
        notifierCourrierData.courrierInfos.forEach {
            val courrierGenere = courrierGeneratorUseCase.executeInternal(
                parametreCourrierInput = ParametreCourrierInput(
                    modeleCourrierId = modeleCourrierId,
                    courrierReference = reference,
                    listParametres = it.courrierParametres.listeParametre.map {
                        NomValue(
                            nom = it.cle,
                            valeur = it.valeur,
                            estRequis = false,
                        )
                    },
                ),
                userInfo = userInfo,
                isApacheHop = true,
            )

            logManager.info("Courrier généré à l'emplacement $courrierGenere")

            val result = createCourrierUseCase.execute(
                userInfo,
                CourrierData(
                    courrierId = UUID.randomUUID(),
                    documentId = UUID.randomUUID(),
                    modeleCourrierId = modeleCourrierId,
                    nomDocumentTmp = courrierGenere.fileName.toString(),
                    listeDestinataire = it.courrierParametres.listeDestinataire,
                    courrierReference = reference,
                    codeThematique = GlobalConstants.THEMATIQUE_POINT_EAU,
                ),
            )

            when (result) {
                is Result.Success,
                is Result.Created,
                -> {
                    logManager.info("Courrier généré : $result (paramètes ${it.courrierParametres.listeParametre})")
                }
                else -> logManager.error("Erreur lors de la génération du courrier : $result (paramètes ${it.courrierParametres.listeParametre})")
            }
        }
    }
}
