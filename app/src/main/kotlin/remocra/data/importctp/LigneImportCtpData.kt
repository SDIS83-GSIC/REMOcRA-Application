package remocra.data.importctp

import remocra.data.enums.ErreurImportCtp

/** Permet de stocker les infos d'une ligne d'un import CTP  */
class LigneImportCtpData {
    var bilan: String? = null
    var bilanStyle: ErreurImportCtp.Gravite? = null

    var codeInsee: String? = null
    var numeroInterne: Int? = null

    var dateCtp: String? = null

    var numeroLigne: Int? = null

    var warnings: MutableList<String> = ArrayList()

    var dataVisite: LigneImportCtpVisiteData? = null

    fun removeWarnings() {
        warnings.clear()
    }

    fun addAllWarnings(warnings: Collection<String>?) {
        this.warnings.addAll(warnings!!)
    }
}
