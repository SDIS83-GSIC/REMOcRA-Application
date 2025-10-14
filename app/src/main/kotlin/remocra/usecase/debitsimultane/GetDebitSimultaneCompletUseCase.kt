package remocra.usecase.debitsimultane

import jakarta.inject.Inject
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.DebitSimultaneMesureData
import remocra.data.enums.ParametreEnum
import remocra.db.DebitSimultaneRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetDebitSimultaneCompletUseCase : AbstractUseCase() {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var parametresProvider: ParametresProvider

    fun execute(userInfo: WrappedUserInfo, debitSimultaneId: UUID): DebitSimultaneComplet {
        val debitSimultaneInfo = debitSimultaneRepository.getDebitSimultane(debitSimultaneId, userInfo.isSuperAdmin, userInfo.zoneCompetence?.zoneIntegrationId)

        val listeDebitSimultaneMesure = debitSimultaneRepository.getDebitSimultaneMesure(
            debitSimultaneId = debitSimultaneId,
        )

        val listePibi = listeDebitSimultaneMesure
            .map { it.listePibi }
            .flatten()

        val maxDiametreCanalisation: Int? =
            listePibi
                .map { it.pibiDiametreCanalisation }
                .sortedByDescending { it }.firstOrNull()

        val typeReseau = listePibi.map { it.typeReseauLibelle }.firstOrNull()

        // On contruit l'objet de retour
        return DebitSimultaneComplet(
            debitSimultaneId = debitSimultaneId,
            debitSimultaneSiteId = debitSimultaneInfo.debitSimultaneSiteId,
            debitSimultaneNumeroDossier = debitSimultaneInfo.debitSimultaneNumeroDossier,
            siteLibelle = debitSimultaneInfo.siteLibelle,
            maxDiametreCanalisation = maxDiametreCanalisation,
            typeReseauLibelle = typeReseau,
            listeDebitSimultaneMesure = listeDebitSimultaneMesure.map {
                DebitSimultaneMesureData(
                    debitSimultaneMesureId = it.debitSimultaneMesureId,
                    debitSimultaneMesureDebitRequis = it.debitSimultaneMesureDebitRequis,
                    debitSimultaneMesureDebitMesure = it.debitSimultaneMesureDebitMesure,
                    debitSimultaneMesureDebitRetenu = it.debitSimultaneMesureDebitRetenu,
                    debitSimultaneMesureDateMesure = it.debitSimultaneMesureDateMesure,
                    debitSimultaneMesureCommentaire = it.debitSimultaneMesureCommentaire,
                    debitSimultaneMesureIdentiqueReseauVille = it.debitSimultaneMesureIdentiqueReseauVille ?: false,
                    listePeiId = it.listePibi.map { it.pibiId },
                    it.documentNomFichier,
                    it.documentId,
                )
            },
            vitesseEau = parametresProvider.getParametreInt(ParametreEnum.VITESSE_EAU.name),
        )
    }

    data class DebitSimultaneComplet(
        val debitSimultaneId: UUID,
        val debitSimultaneSiteId: UUID?,
        val debitSimultaneNumeroDossier: String,
        val siteLibelle: String?,
        val maxDiametreCanalisation: Int?,
        val typeReseauLibelle: String?,
        val listeDebitSimultaneMesure: Collection<DebitSimultaneMesureData>,
        val vitesseEau: Int?,
    )
}
