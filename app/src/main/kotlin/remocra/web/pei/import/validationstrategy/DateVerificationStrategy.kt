package remocra.web.pei.import.validationstrategy

import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErreurImportPei
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Vérification de la date GPS
class DateVerificationStrategy : VerificationStrategy {

    companion object {
        const val PATTERN_PEI_IMPORT: String = "d/M/yy H:mm"
    }

    private fun importDatePeiToDate(initialDate: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(PATTERN_PEI_IMPORT, Locale.getDefault())
        return LocalDateTime.parse(initialDate, formatter)
    }

    override fun validate(row: ImportPeiParametre, data: LigneImportPeiData, userInfo: WrappedUserInfo) {
        if (!row.date_gps.isNullOrBlank()) {
            try {
                val date = importDatePeiToDate(row.date_gps)
                data.currentDate = date
                data.dateReleve = date.toString()
            } catch (_: Exception) {
                data.addWarning(ErreurImportPei.ERR_DATE_MAL_FORMEE.libelleLong)
            }
        }
    }
}
