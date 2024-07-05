package remocra.usecases.pei

import com.google.inject.Inject
import remocra.app.DataCacheProvider
import remocra.authn.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.PeiData
import remocra.data.PeiForCalculDispoData
import remocra.data.PeiForNumerotationData
import remocra.data.PenaData
import remocra.data.PibiData
import remocra.data.enums.TypeSourceModification
import remocra.db.VisiteRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.TypePei
import remocra.eventbus.EventBus
import remocra.eventbus.pei.PeiModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.usecases.AbstractCUDUseCase
import remocra.web.pei.CalculDispoUseCase
import remocra.web.pei.NumerotationUseCase
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * Classe mère des useCases des opérations C, U, D des PEI.
 * Permet de gérer les opérations transverses, calcul de la numérotation, de la dispo, et déclenchement des events, communes aux différents types d'opérations
 */
abstract class AbstractCUDPeiUseCase(private val typeOperation: TypeOperation) : AbstractCUDUseCase<PeiData>() {
    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var calculNumerotationUseCase: NumerotationUseCase

    @Inject
    lateinit var calculDispoUseCase: CalculDispoUseCase

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var visiteRepository: VisiteRepository

    override fun postEvent(element: PeiData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.peiId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.PEI,
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.idUtilisateur, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = OffsetDateTime.now(ZoneId.systemDefault()),
            ),
        )
        eventBus.post(PeiModifiedEvent(element.peiId))
    }

    override fun execute(element: PeiData): PeiData {
// TODO en fonction du futur paramétrage "activer la renumérotation auto" et des propriétés modifiées, définir
        val needComputeNumero = false

        if (typeOperation != TypeOperation.DELETE) {
            if (needComputeNumero) {
                // Création de l'objet data pour le calcul
                val peiForNumerotationData = PeiForNumerotationData(
                    peiNumeroInterne = null,
                    peiId = element.peiId,
                    peiCommuneId = element.peiCommuneId,
                    peiZoneSpecialeId = element.peiZoneSpecialeId,
                    peiNatureDeciId = element.peiNatureDeciId,
                    peiDomaineId = element.peiDomaineId,
                )

                // Calcul du numéro *interne*
                val numeroInterne = calculNumerotationUseCase.computeNumeroInterne(peiForNumerotationData)
                peiForNumerotationData.peiNumeroInterne = numeroInterne

                // Calcul du numéro *complet*, avec un numéro interne mis à jour
                val numeroComplet = calculNumerotationUseCase.computeNumero(peiForNumerotationData)

                element.peiNumeroInterne = numeroInterne
                element.peiNumeroComplet = numeroComplet
            }

            // à partir de là, on a besoin de travailler sur le type concret
            if (TypePei.PIBI == dataCacheProvider.getNatures()[element.peiNatureId]!!.natureTypePei) {
                val elementConcret = element as PibiData

                val lastVisite = visiteRepository.getLastVisiteDebitPression(element.peiId)

                // Calcul de la dispo du PEI
                val peiForCalculDispoData = PeiForCalculDispoData(
                    peiId = elementConcret.peiId,
                    peiNatureId = elementConcret.peiNatureId,
                    diametreId = elementConcret.pibiDiametreId,
                    debit = lastVisite?.visiteCtrlDebitPressionDebit,
                    pression = lastVisite?.visiteCtrlDebitPressionPression?.toDouble(),
                    pressionDynamique = lastVisite?.visiteCtrlDebitPressionPressionDyn?.toDouble(),
                    penaCapacite = null,
                    penaCapaciteIllimitee = null,
                )

                elementConcret.peiDisponibiliteTerrestre = calculDispoUseCase.execute(peiForCalculDispoData)
            } else {
                val elementConcret = element as PenaData

                // Calcul de la dispo du PEI
                val peiForCalculDispoData = PeiForCalculDispoData(
                    peiId = elementConcret.peiId,
                    peiNatureId = elementConcret.peiNatureId,
                    diametreId = null,
                    debit = null,
                    pression = null,
                    pressionDynamique = null,
                    penaCapacite = elementConcret.penaCapacite,
                    penaCapaciteIllimitee = elementConcret.penaCapaciteIllimitee,
                )

                elementConcret.peiDisponibiliteTerrestre = calculDispoUseCase.execute(peiForCalculDispoData)
            }
        }

        // Tout est à jour, on peut enregistrer l'élément :
        executeSpecific(element)

        // On rend la main au parent pour la logique d'événements
        return element
    }

    /**
     * Méthode permettant de décrire tout ce qui est spécifique à chaque opération, typiquement le service métier à appeler
     */
    abstract fun executeSpecific(element: PeiData): Any?
}
