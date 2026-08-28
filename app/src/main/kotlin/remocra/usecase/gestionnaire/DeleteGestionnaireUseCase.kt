package remocra.usecase.gestionnaire

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.SiteData
import remocra.data.enums.ErrorType
import remocra.db.ContactRepository
import remocra.db.GestionnaireRepository
import remocra.db.SiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.pei.PeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase

class DeleteGestionnaireUseCase
@Inject
constructor(
    private val gestionnaireRepository: GestionnaireRepository,
    private val siteRepository: SiteRepository,
    private val updateSiteUseCase: UpdateSiteUseCase,
    private val peiUseCase: PeiUseCase,
    private val updatePeiUseCase: UpdatePeiUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val contactRepository: ContactRepository,
) :
    AbstractCUDUseCase<Gestionnaire>(TypeOperation.DELETE) {

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.GEST_SITE_A)) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_FORBIDDEN_DELETE)
        }
    }

    override fun postEvent(element: Gestionnaire, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.gestionnaireId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.GESTIONNAIRE,
                auteurTracabilite = userInfo.getInfosTracabilite(),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: WrappedUserInfo, element: Gestionnaire): Gestionnaire {
        val gestionnaireId = element.gestionnaireId

        // Suppression des liens PEI <-> Gestionnaire
        gestionnaireRepository.getPeiIdByGestionnaireId(gestionnaireId).forEach { peiId ->
            peiUseCase.getInfoPei(peiId).let { peiData ->
                val peiWithoutGestionnaire = when (peiData) {
                    is PibiData -> peiData.copy(peiGestionnaireId = null, peiSiteId = null)
                    is PenaData -> peiData.copy(peiGestionnaireId = null, peiSiteId = null)
                    else -> throw RemocraResponseException(ErrorType.GESTIONNAIRE_ERROR_ON_DELETE, "Type de PEI non supporté : ${peiData.peiTypePei}")
                }
                updatePeiUseCase.execute(userInfo, peiWithoutGestionnaire, transactionManager).ensureSuccess()
            }
        }

        // Suppression des liens Site <-> Gestionnaire
        gestionnaireRepository.getSiteIdByGestionnaireId(gestionnaireId).forEach { siteId ->
            siteRepository.getById(siteId).let { site ->
                updateSiteUseCase.execute(
                    userInfo,
                    SiteData(
                        siteId = site.siteId,
                        siteCode = site.siteCode,
                        siteLibelle = site.siteLibelle,
                        siteGestionnaireId = null,
                        siteActif = site.siteActif,
                    ),
                    transactionManager,
                ).ensureSuccess()
            }
        }

        // Suppression des Contacts
        gestionnaireRepository.getContactsIdForGestionnaire(gestionnaireId).forEach { contactId ->
            deleteContactUseCase.execute(
                userInfo,
                contactRepository.getById(contactId, isGestionnaire = true).copy(isGestionnaire = true),
                transactionManager,
            ).ensureSuccess()
        }

        // Suppression du Gestionnaire
        gestionnaireRepository.deleteGestionnaire(gestionnaireId)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: Gestionnaire) {
        // no-op
    }

    private fun Result.ensureSuccess() {
        if (this !is Result.Success) {
            throw RemocraResponseException(ErrorType.GESTIONNAIRE_ERROR_ON_DELETE, this.toString())
        }
    }
}
