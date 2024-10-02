package remocra.usecase.courrier

import com.google.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.courrier.form.NomValue
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.data.courrier.parametres.CourrierParametresRopData
import remocra.db.ModeleCourrierRepository
import remocra.db.jooq.remocra.enums.TypeParametreCourrier
import remocra.usecase.document.DocumentUtils
import java.nio.file.Paths
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class CourrierGenerator {

    @Inject
    lateinit var courrierRopGenerator: CourrierRopGenerator

    @Inject
    lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var clock: Clock

    fun execute(parametreCourrierInput: ParametreCourrierInput, userInfo: UserInfo?, uriBuilder: UriBuilder): UrlCourrier? {
        val modeleCourrier = modeleCourrierRepository.getById(parametreCourrierInput.modeleCourrierId)
        val file = if (modeleCourrier.modeleCourrierCode == GlobalConstants.COURRIER_CODE_ROP) {
            courrierRopGenerator.execute(
                CourrierParametresRopData(
                    communeId = getValueUUID(parametreCourrierInput.listParametres, TypeParametreCourrier.COMMUNE_ID.name),
                    gestionnaireId = getValueUUID(parametreCourrierInput.listParametres, TypeParametreCourrier.GESTIONNAIRE_ID.name),
                    isOnlyPublic = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.IS_ONLY_PUBLIC.name)?.toBooleanStrictOrNull(),
                    isEPCI = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.IS_EPCI.name)?.toBooleanStrictOrNull(),
                    profilUtilisateurId = getValueUUID(parametreCourrierInput.listParametres, TypeParametreCourrier.PROFIL_UTILISATEUR_ID.name),
                    annee = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.ANNEE.name),
                    expediteurGrade = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.EXPEDITEUR_GRADE.name),
                    expediteurStatut = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.EXPEDITEUR_STATUT.name),
                    reference = getValue(parametreCourrierInput.listParametres, TypeParametreCourrier.REFERENCE.name),
                    cis = getValueUUID(parametreCourrierInput.listParametres, TypeParametreCourrier.CIS_ID.name),
                ),
                userInfo,
            )
        } else {
            null
        }
        // TODO générer les autres courrier ici

        if (file != null) {
            val nomFichier = "${modeleCourrier.modeleCourrierCode}-${ZonedDateTime.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}.pdf"
            documentUtils.saveFile(file, nomFichier, GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE)
            return UrlCourrier(
                url = uriBuilder
                    .queryParam("courrierPath", Paths.get(GlobalConstants.DOSSIER_DOCUMENT_TEMPORAIRE, nomFichier))
                    .build()
                    .toString(),
            )
        }

        return null
    }

    data class UrlCourrier(
        val url: String,
    )

    private fun getValue(listParametres: List<NomValue>?, nomParametre: String) =
        listParametres?.firstOrNull { it.nom == nomParametre }?.valeur

    private fun getValueUUID(listParametres: List<NomValue>?, nomParametre: String): UUID? {
        val value = getValue(listParametres, nomParametre)
        if (value != null) {
            return UUID.fromString(value)
        }
        return null
    }
}
