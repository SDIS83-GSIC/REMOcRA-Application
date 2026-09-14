package remocra.usecase.tournee

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.DestinataireData
import remocra.data.TypeDestinataire
import remocra.data.courrier.form.NomValue
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeAutoriteDeci
import remocra.data.enums.TypeServicePublicDeci
import remocra.db.ModeleCourrierRepository
import remocra.db.OrganismeRepository
import remocra.db.TourneeRepository
import remocra.db.jooq.remocra.enums.TypeCourrier
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.utils.GenererRapportCourrierUseCase
import java.util.UUID

class GenererRapportPostRopUseCase @Inject constructor(
    private val genererCourrierUseCase: GenererRapportCourrierUseCase,
    private val tourneeRepository: TourneeRepository,
    private val modeleCourrierRepository: ModeleCourrierRepository,
    private val organismeRepository: OrganismeRepository,
    private val dataCacheProvider: DataCacheProvider,
) : AbstractUseCase() {

    fun execute(tourneeId: UUID, userInfo: WrappedUserInfo): Result {
        val modeleCourrier = modeleCourrierRepository.getByType(TypeCourrier.RAPPORT_POST_ROP)
            ?: throw RemocraResponseException(ErrorType.RAPPORT_POST_ROP_MODELE_INEXISTANT)

        val listPeiTournee = tourneeRepository.getListPeiByListTournee(listOf(tourneeId))[tourneeId]
        if (listPeiTournee.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.RAPPORT_POST_ROP_NO_PEI)
        }

        val typesOrganismesANotifier = TypeAutoriteDeci.entries
            .map { it.valeurConstante }
            .plus(TypeServicePublicDeci.entries.map { it.valeurConstante })
            .toSet()

        val listIdTypeOrganismeANotifier = dataCacheProvider.getTypesOrganisme().values
            .filter { it.typeOrganismeCode in typesOrganismesANotifier }
            .map { it.typeOrganismeId }

        // Sur ces PEI, en fonction des cas, on extrait les organismes à notifier
        // Déjà les SP_DECI
        val listeDestinataire = organismeRepository.getDestinatairesContactOrganisme(
            listePeiId = listPeiTournee.map { it.peiId },
            typeOrganisme = listIdTypeOrganismeANotifier,
            contactRole = GlobalConstants.CONTACT_ROLE_RAPPORT_POST_ROP,
        ).map {
            DestinataireData(
                destinataireId = it.key.destinataireId
                    ?: throw RemocraResponseException(ErrorType.RAPPORT_POST_ROP_ERREUR_GENERATION),
                typeDestinataire = TypeDestinataire.CONTACT_ORGANISME.libelle,
                nomDestinataire = it.key.destinataireNom ?: "",
                emailDestinataire = it.key.destinataireEmail,
                fonctionDestinataire = it.key.destinataireFonction ?: "",
            )
        }

        val result = genererCourrierUseCase.execute(
            modeleCourrier = modeleCourrier,
            courrierReference = "${modeleCourrier.modeleCourrierLibelle}_$tourneeId",
            destinataires = listeDestinataire,
            userInfo = userInfo,
            parametres = listOf(NomValue("TOURNEE_ID", tourneeId.toString(), false)),
        )

        when (result) {
            is Result.Success,
            is Result.Created,
            -> {
                // On met à jour la tournée si la notification du courrier a réussi
                tourneeRepository.updateDateDerniereRealisationRop(tourneeId)
                return Result.Success("Rapport généré pour la tournée $tourneeId")
            }

            else -> throw RemocraResponseException(ErrorType.RAPPORT_POST_ROP_ERREUR_GENERATION)
        }
    }
}
