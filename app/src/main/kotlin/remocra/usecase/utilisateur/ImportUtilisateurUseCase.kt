package remocra.usecase.utilisateur

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.csv.CsvReader
import remocra.data.LigneImportUtilisateur
import remocra.data.UtilisateurData
import remocra.data.enums.ErrorType
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.utilisateur.importvalidationstrategy.VerificationUserDataUseCase
import java.io.InputStream
import java.util.UUID

class ImportUtilisateurUseCase @Inject constructor(
    private val csvReader: CsvReader,
    private val verificationData: VerificationUserDataUseCase,
    private val createUtilisateurUseCase: CreateUtilisateurUseCase,
) : AbstractUseCase() {

    companion object {
        const val CSV_DELIMITER: Char = ','
        const val CSV_TYPED_SCHEMA: Boolean = false
    }

    fun verifierDonnees(userInfo: WrappedUserInfo, inputStream: InputStream): LigneImportUserData {
        val data = LigneImportUserData()
        data.utilisateurList = (csvReader.readCsvFile(inputStream, CSV_DELIMITER, CSV_TYPED_SCHEMA))

        if (data.utilisateurList.isNullOrEmpty()) {
            throw RemocraResponseException(ErrorType.UTILISATEUR_IMPORT_EMPTY_FILE)
        }

        data.utilisateurList!!.forEachIndexed { index, utilisateur ->
            verificationData.execute(row = utilisateur, ligne = index + 1, userInfo = userInfo, data = data)
        }

        return data
    }

    fun importUtilisateurEnregistrement(userInfo: WrappedUserInfo, utilisateursList: List<LigneImportUtilisateur>) {
        utilisateursList.forEach { utilisateur ->
            // les données sont vérifiés, mais au cas où, on peut revérifier l'existence ici :
            if (utilisateur.mail.isNullOrEmpty() || utilisateur.identifiant.isNullOrEmpty()) {
                throw RemocraResponseException(ErrorType.UTILISATEUR_IMPORT_EMPTY_ROW)
            } else {
                createUtilisateurUseCase.execute(
                    userInfo,
                    UtilisateurData(
                        utilisateurId = UUID.randomUUID(),
                        utilisateurActif = utilisateur.actif ?: false,
                        utilisateurEmail = utilisateur.mail ?: "",
                        utilisateurNom = utilisateur.nom ?: "",
                        utilisateurPrenom = utilisateur.prenom ?: "",
                        utilisateurUsername = utilisateur.identifiant ?: "",
                        utilisateurTelephone = utilisateur.telephone,
                        utilisateurCanBeNotified = utilisateur.notifie,
                        utilisateurProfilUtilisateurId = utilisateur.profilUtilisateurId,
                        utilisateurOrganismeId = utilisateur.organismeId,
                        utilisateurIsSuperAdmin = false,
                    ),
                )
            }
        }
    }
}
