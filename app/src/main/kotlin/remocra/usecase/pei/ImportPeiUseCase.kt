package remocra.usecase.pei

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.csv.CsvReader
import remocra.data.enums.ErreurImportPei
import remocra.usecase.AbstractUseCase
import remocra.web.pei.import.ImportPeiData
import remocra.web.pei.import.ImportPeiParametre
import remocra.web.pei.import.LigneImportPeiData
import remocra.web.pei.import.validationstrategy.VerificationChain
import java.io.InputStream

class ImportPeiUseCase @Inject constructor(
    private val csvReader: CsvReader,
    private val verificationChain: VerificationChain,
) : AbstractUseCase() {

    companion object {
        const val CSV_DELIMITER: Char = ','
        const val CSV_TYPED_SCHEMA: Boolean = false
        const val INFO_PEI_VALIDE: String = "PEI Validé"
    }

    fun importPeiValidation(file: InputStream, userInfo: WrappedUserInfo): ImportPeiData {
        val data = ImportPeiData()
        val records: List<ImportPeiParametre>? = csvReader.readCsvFile(file, CSV_DELIMITER, CSV_TYPED_SCHEMA)

        if (records.isNullOrEmpty()) {
            data.bilanVerifications.add(buildErrorData(ErreurImportPei.ERR_FICHIER_INACCESSIBLE))
            return data
        }

        records.forEachIndexed { index, record ->
            val ligne = LigneImportPeiData().apply {
                numeroLigne = index + 1
                numeroPei = record.numero
                observation = record.observation
                epsg = record.epsg
            }
            verificationChain.execute(record, ligne, userInfo)
            data.bilanVerifications.add(ligne)
        }

        updateBilanStyle(data)

        return data
    }

    private fun updateBilanStyle(data: ImportPeiData) {
        data.bilanVerifications.forEach {
            if (it.hasWarnings()) {
                it.bilanStyle = ErreurImportPei.Gravite.WARNING
            }
            if (it.hasErrors()) {
                it.bilanStyle = ErreurImportPei.Gravite.ERROR
            }
            if (!it.hasWarnings() && !it.hasErrors()) {
                it.addInfos(INFO_PEI_VALIDE)
            }
        }
    }

    private fun buildErrorData(erreur: ErreurImportPei): LigneImportPeiData {
        return LigneImportPeiData().apply {
            numeroLigne = 0
            bilanStyle = erreur.gravite
            addInfos(erreur.libelleLong)
        }
    }
}
