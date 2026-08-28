package remocra.usecase.visites

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.GlobalConstants
import remocra.auth.WrappedUserInfo
import remocra.data.DestinataireData
import remocra.data.TypeDestinataire
import remocra.data.VisiteData
import remocra.data.courrier.form.NomValue
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.db.ModeleCourrierRepository
import remocra.db.OrganismeRepository
import remocra.db.ParametreRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.enums.TypeCourrier
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.utils.GenererRapportCourrierUseCase
import remocra.utils.getListOfString
import java.util.UUID
import kotlin.collections.map

class GenererRapportVisiteROIUseCase @Inject constructor(
    private val genererCourrierUseCase: GenererRapportCourrierUseCase,
    private val modeleCourrierRepository: ModeleCourrierRepository,
    private val parameterRepository: ParametreRepository,
    private val organismeRepository: OrganismeRepository,
    private val objectMapper: ObjectMapper,
) : AbstractUseCase() {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(element: VisiteData, userInfo: WrappedUserInfo, transactionManager: TransactionManager): Result? {
        val modeleCourrier = modeleCourrierRepository.getByType(TypeCourrier.COURRIER_ROI)
            ?: throw RemocraResponseException(ErrorType.RAPPORT_VISITE_ROI_MODEL_INEXISTANT)

        val listIdTypeOrganismeANotifier = parameterRepository.getMapParametres()
            .getListOfString(ParametreEnum.PEI_ORGANISME_NOTIFICATION_ROI.name, objectMapper)
            ?.map(UUID::fromString) ?: run {
            logger.warn("Aucun organisme trouvé : pas d'envoi de mail de création de visite de type ROI.")
            return null
        }

        val listeDestinataire = organismeRepository.getDestinatairesContactOrganisme(
            peiId = element.visitePeiId,
            listTypeOrganismeId = listIdTypeOrganismeANotifier,
            contactRole = GlobalConstants.CONTACT_ROLE_DESTINATAIRE_COURRIER_ROI_VISITE_RECEPTION,
        ).map {
            DestinataireData(
                destinataireId = it.destinataireId ?: throw RemocraResponseException(ErrorType.COURRIER_VISITE_ROI_ERREUR),
                typeDestinataire = TypeDestinataire.CONTACT_ORGANISME.libelle,
                nomDestinataire = it.destinataireNom ?: "",
                emailDestinataire = it.destinataireEmail,
                fonctionDestinataire = it.destinataireFonction ?: "",
            )
        }

        return genererCourrierUseCase.execute(
            modeleCourrier = modeleCourrier,
            courrierReference = "${modeleCourrier.modeleCourrierLibelle}_${element.visiteId}",
            parametres = listOf(NomValue("PEI_ID", element.visitePeiId.toString(), true)),
            destinataires = listeDestinataire,
            userInfo = userInfo,
            transactionManager = transactionManager,
            peiId = element.visitePeiId,
        )
    }
}
