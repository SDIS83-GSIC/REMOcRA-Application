package remocra.web.pei.import

import remocra.data.PeiData
import remocra.data.enums.ErreurImportPei
import java.time.LocalDateTime
import java.util.UUID

class LigneImportPeiData {
    var bilanStyle: ErreurImportPei.Gravite = ErreurImportPei.Gravite.OK
    var numeroLigne: Int? = null
    var warnings: MutableList<String> = mutableListOf()
    var infos: MutableList<String> = mutableListOf()

    var dateReleve: String? = null
    var numeroPei: String? = null
    var coordonneeX: Double = 0.0
    var coordonneeY: Double = 0.0
    var epsg: String? = null
    var observation: String? = null

    var currentPeiId: UUID? = null
    var currentDate: LocalDateTime? = null
    var currentPeiData: PeiData? = null

    fun addWarning(warning: String) = warnings.add(warning)
    fun hasWarnings() = warnings.isNotEmpty()
    fun hasErrors() = bilanStyle == ErreurImportPei.Gravite.ERROR
    fun addInfos(info: String) = infos.add(info)
}

data class ImportPeiParametre(
    val epsg: String? = null,
    val x: Double? = null,
    val y: Double? = null,
    val numero: String? = null,
    val observation: String? = null,
    val date_gps: String? = null,
)

class ImportPeiData {
    var bilanVerifications: MutableList<LigneImportPeiData> = arrayListOf()
}
