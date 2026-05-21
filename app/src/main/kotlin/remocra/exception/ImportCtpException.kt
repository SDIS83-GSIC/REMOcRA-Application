package remocra.exception

import remocra.data.enums.ErreurImportCtp
import remocra.data.importctp.LigneImportCtpData

class ImportCtpException(private val typeErreur: ErreurImportCtp, private val data: LigneImportCtpData) : Exception() {

    fun getTypeErreur(): ErreurImportCtp {
        return typeErreur
    }

    fun getData(): LigneImportCtpData {
        return this.data
    }
}
