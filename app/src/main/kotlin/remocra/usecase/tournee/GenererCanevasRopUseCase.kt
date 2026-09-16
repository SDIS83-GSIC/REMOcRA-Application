package remocra.usecase.tournee

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.WrappedUserInfo
import remocra.data.courrier.form.NomValue
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.data.enums.ErrorType
import remocra.db.ModeleCourrierRepository
import remocra.db.TourneeRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.courrier.CourrierGeneratorUseCase
import java.nio.file.Path
import java.util.UUID

class GenererCanevasRopUseCase @Inject constructor(
    private val courrierGeneratorUseCase: CourrierGeneratorUseCase,
    private val tourneeRepository: TourneeRepository,
    private val modeleCourrierRepository: ModeleCourrierRepository,

) : AbstractUseCase() {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo): Path {
        val modeleCourrier = modeleCourrierRepository.getCanevasRop()
            ?: throw RemocraResponseException(ErrorType.CANEVAS_ROP_MODELE_INEXISTANT)

        val tourneeLibelle = tourneeRepository.getTourneeLibelleById(tourneeId)

        return try { courrierGeneratorUseCase.executeInternal(
            ParametreCourrierInput(
                modeleCourrierId = modeleCourrier.modeleCourrierId,
                courrierReference = tourneeLibelle,
                listParametres = listOf(
                    NomValue("TOURNEE_ID", tourneeId.toString(), true),
                ),
            ),
            userInfo,
        )
        } catch (e: Exception) {
            logger.error("Erreur de génération du canevas ROP", e)
            throw RemocraResponseException(ErrorType.CANEVAS_ROP_ERREUR_GENERATION)
        }
    }
}
