package remocra.exception

import remocra.data.enums.ErreurImportPei
import remocra.web.pei.import.LigneImportPeiData

class ImportPeiException(
    private val typeErreur: ErreurImportPei,
    private val data: LigneImportPeiData,
) : Exception() {

    fun getTypeErreur(): ErreurImportPei {
        return typeErreur
    }

    fun getData(): LigneImportPeiData {
        return this.data
    }
}
