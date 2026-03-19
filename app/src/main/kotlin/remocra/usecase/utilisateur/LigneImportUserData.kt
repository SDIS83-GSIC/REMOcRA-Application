package remocra.usecase.utilisateur

import remocra.data.LigneImportUtilisateur

data class ImportError(
    val line: Int,
    val message: String,
)

class LigneImportUserData {
    var errors: MutableList<ImportError> = mutableListOf()
    var utilisateurList: MutableList<LigneImportUtilisateur>? = null

    fun addError(line: Int, message: String) {
        errors.add(ImportError(line, message))
    }
}
