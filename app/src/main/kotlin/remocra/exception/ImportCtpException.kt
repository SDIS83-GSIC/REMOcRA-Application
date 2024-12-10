package remocra.exception

import remocra.data.enums.ErreurImportCtp
import remocra.data.importctp.LigneImportCtpData

class ImportCtpException(typeErreur: ErreurImportCtp, data: LigneImportCtpData) : Exception() {
    private val typeErreur: ErreurImportCtp = typeErreur

    private val data: LigneImportCtpData = data

    fun getTypeErreur(): ErreurImportCtp {
        return typeErreur
    }

    fun getData(): LigneImportCtpData {
        return this.data
    }
}
