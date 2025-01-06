package remocra.usecase.importctp

import jakarta.inject.Inject
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.enums.ErrorType
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.UUID

class ExportCtpUseCase : AbstractUseCase() {

    @Inject lateinit var peiRepository: PeiRepository

    @Inject lateinit var anomalieRepository: AnomalieRepository

    @Inject lateinit var parametresProvider: ParametresProvider

    companion object {
        private const val ONGLET_SAISIE_INDEX = 1
        private const val ONGLET_ANOMALIE_INDEX = 2
        private const val NB_LIGNES_RESERVEES = 4
        private const val CELL_CODE_SDIS_INDEX = 0
        private const val CELL_COMMUNE_INDEX = 1
        private const val CELL_CODE_INSEE_INDEX = 2
        private const val CELL_PEI_NUM_INDEX = 3
        private const val CELL_ADRESSE_INDEX = 4
        private const val CELL_LATITUDE_INDEX = 5
        private const val CELL_LONGITUDE_INDEX = 6
        private const val CELL_NATURE_DECI_INDEX = 7
        private const val CELL_TYPE_INDEX = 8
    }

    fun execute(communeId: UUID?, userInfo: UserInfo): ByteArray {
        /** Création du fichier excel */
        // Récupération du fichier modèle
        val templatePath = GlobalConstants.TEMPLATE_EXPORT_CTP_FULL_PATH

        val inputStream: FileInputStream
        try {
            inputStream = FileInputStream(File(templatePath))
        } catch (e: FileNotFoundException) {
            throw RemocraResponseException(ErrorType.IMPORT_CTP_NO_TEMPLATE_PROVIDED)
        }

        val workbook = XSSFWorkbook(inputStream)

        /** Récupération des données PIBI */
        val pibiDataToExport = peiRepository.exportCTP(communeId, userInfo.organismeId)

        /** Écriture des données PIBI */
        // Accéder à la deuxième feuille
        var sheet = workbook.getSheetAt(ONGLET_SAISIE_INDEX)

        var currentRow: XSSFRow
        var cpt = 0

        while (cpt < pibiDataToExport.size) {
            currentRow = sheet.getRow(NB_LIGNES_RESERVEES + cpt) ?: sheet.createRow(NB_LIGNES_RESERVEES + cpt)
            // Colonne 0 = A => "Code SDIS" => peiId
            // Initialisation de la cellule que l'on souhaite éditer
            var currentCell = currentRow.getCell(CELL_CODE_SDIS_INDEX) ?: currentRow.createCell(CELL_CODE_SDIS_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].peiId.toString())
            // Colonne 1 = B => "Commune" => communeLibelle
            currentCell = currentRow.getCell(CELL_COMMUNE_INDEX) ?: currentRow.createCell(CELL_COMMUNE_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].communeLibelle)
            // Colonne 2 = C => "Code INSEE" => communeCodeInsee
            currentCell = currentRow.getCell(CELL_CODE_INSEE_INDEX) ?: currentRow.createCell(CELL_CODE_INSEE_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].communeCodeInsee)
            // Colonne 3 = D => "N° de PEI" => peiNumeroInterne
            currentCell = currentRow.getCell(CELL_PEI_NUM_INDEX) ?: currentRow.createCell(CELL_PEI_NUM_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].peiNumeroInterne.toString())
            // Colonne 4 = E => "Adresse" => concatAdresse
            currentCell = currentRow.getCell(CELL_ADRESSE_INDEX) ?: currentRow.createCell(CELL_ADRESSE_INDEX)
            var concatAdresse: String = ""
            if (pibiDataToExport[cpt].peiEnFace) { concatAdresse += "En face du " }
            if (pibiDataToExport[cpt].peiNumeroVoie != null) { concatAdresse += pibiDataToExport[cpt].peiNumeroVoie }
            if (pibiDataToExport[cpt].peiSuffixeVoie != null) { concatAdresse += pibiDataToExport[cpt].peiSuffixeVoie }
            concatAdresse += ' '
            if (pibiDataToExport[cpt].voieLibelle != null) {
                concatAdresse += pibiDataToExport[cpt].voieLibelle
            } else if (pibiDataToExport[cpt].peiVoieTexte != null) {
                concatAdresse += pibiDataToExport[cpt].peiVoieTexte
            }
            currentCell.setCellValue(concatAdresse.trim())
            // Colonne 5 = F => "Latitude" => peiLatitude
            currentCell = currentRow.getCell(CELL_LATITUDE_INDEX) ?: currentRow.createCell(CELL_LATITUDE_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].peiLatitude.toBigDecimal().toPlainString())
            // Colonne 6 = G => "Longitude" => peiLongitude
            currentCell = currentRow.getCell(CELL_LONGITUDE_INDEX) ?: currentRow.createCell(CELL_LONGITUDE_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].peiLongitude.toBigDecimal().toPlainString())
            // Colonne 7 = H => "Statut du PEI" => peiLongitude
            currentCell = currentRow.getCell(CELL_NATURE_DECI_INDEX) ?: currentRow.createCell(CELL_NATURE_DECI_INDEX)
            currentCell.setCellValue(pibiDataToExport[cpt].natureDeciLibelle)
            // Colonne 8 = I => "Type" => peiLongitude
            currentCell = currentRow.getCell(CELL_TYPE_INDEX) ?: currentRow.createCell(CELL_TYPE_INDEX)
            val concatType = pibiDataToExport[cpt].natureLibelle + " " + pibiDataToExport[cpt].diametreLibelle
            currentCell.setCellValue(concatType.trim())
            // Passage à la donnée/ligne suivante
            cpt++
        }

        /** Récupération des données Anomalies */
        val anomalieDataToExport = anomalieRepository.getAnomalieForExportCTP()
        /** Écriture des données Anomalies */
        // Accéder à la troisième feuille
        sheet = workbook.getSheetAt(ONGLET_ANOMALIE_INDEX)
        cpt = 0
        while (cpt < anomalieDataToExport.size) {
            currentRow = sheet.getRow(cpt) ?: sheet.createRow(cpt)
            // Colonne 0 = A => anomalieCode
            var currentCell = currentRow.getCell(0) ?: currentRow.createCell(0)
            currentCell.setCellValue(anomalieDataToExport[cpt].code)
            // Colonne 1 = B => anomalieLibelle
            currentCell = currentRow.getCell(1) ?: currentRow.createCell(1)
            currentCell.setCellValue(anomalieDataToExport[cpt].libelle)
            // Colonne 2 = C => anomalieCode - anomalieLibelle
            currentCell = currentRow.getCell(2) ?: currentRow.createCell(2)
            currentCell.setCellValue(anomalieDataToExport[cpt].libelle + "-" + anomalieDataToExport[cpt].code)
            cpt++
        }

        // Récupérer la plage nommée "ANOMALIES"
        val namedCell = workbook.allNames.firstOrNull { it.nameName == "ANOMALIES" }
            ?: throw IllegalArgumentException("La plage nommée 'ANOMALIES' est introuvable.")
        // Modification de la plage nommée
        namedCell.refersToFormula = "anomalies!\$C$1:\$C$" + anomalieDataToExport.size

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()

        /** Conversion du flux en tableau de bytes */
        val excelBytes = outputStream.toByteArray()
        return excelBytes
    }
}
