package remocra.usecase.importctp

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import remocra.CoordonneesXYSrid
import remocra.GlobalConstants
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.CreationVisiteCtrl
import remocra.data.VisiteData
import remocra.data.enums.ErreurImportCtp
import remocra.data.enums.ErrorType
import remocra.data.enums.ParametreEnum
import remocra.data.importctp.ImportCtpData
import remocra.data.importctp.LigneImportCtpData
import remocra.data.importctp.LigneImportCtpVisiteData
import remocra.db.AnomalieRepository
import remocra.db.PeiRepository
import remocra.db.TransactionManager
import remocra.db.VisiteRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.exception.ImportCtpException
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import remocra.usecase.pei.MovePeiUseCase
import remocra.usecase.pei.UpdatePeiUseCase
import remocra.usecase.visites.CreateVisiteUseCase
import remocra.utils.checkZoneCompetence
import java.io.InputStream
import java.time.ZonedDateTime
import java.util.Locale
import java.util.UUID

class ImportCtpUseCase : AbstractUseCase() {

    companion object {
        // Constantes correspondant à la structure du fichier d'entrée
        private const val ONGLET_SAISIE = "Saisies_resultats_CT"
        private const val ONGLET_SAISIE_INDEX = 1
        private const val NB_LIGNES_RESERVEES = 4
        private const val LATITUDE_INDEX = 5
        private const val LONGITUDE_INDEX = 6
        private const val DEBUT_CTP_INDEX = 9
        private const val FIN_CTP_INDEX = 18
        private const val CTP_DATE_INDEX = 9
        private const val CTP_AGENT1_INDEX = 10
        private const val CTP_PRESSION_INDEX = 11
        private const val CTP_DEBIT_INDEX = 12
        private const val CTP_DEBUT_ANOMALIE_INDEX = 13
        private const val CTP_FIN_ANOMALIE_INDEX = 17
        private const val CTP_OBSERVATIONS_INDEX = 18

        private const val PRESSION_MAX = 20.0

        private const val ANOMALIES_SEPARATEUR = "-"
    }

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var peiRepository: PeiRepository

    @Inject
    lateinit var visiteRepository: VisiteRepository

    @Inject
    lateinit var anomalieRepository: AnomalieRepository

    @Inject
    lateinit var updatePeiUseCase: UpdatePeiUseCase

    @Inject
    lateinit var movePeiUseCase: MovePeiUseCase

    @Inject
    lateinit var createVisiteUseCase: CreateVisiteUseCase

    @Inject
    lateinit var transactionManager: TransactionManager

    /**
     * Crée un noeud d'erreur global
     *
     * @param mapTypesErreur La map des erreurs possibles récupérée depuis la BDD
     * @param erreur ErreurImportCtp à présenter
     * @return LigneImportCtpData
     */
    private fun buildGlobalErrorData(
        erreur: ErreurImportCtp,
    ): LigneImportCtpData {
        val ligneData = LigneImportCtpData()
        ligneData.numeroLigne = 0
        ligneData.bilanStyle = erreur.gravite
        ligneData.bilan = erreur.libelleLong

        return ligneData
    }

    /**
     * Lors de l'import des visites CTP via un fichier .xls ou .xlsx, vérifie la validité des données
     *
     * @param file Le flux pointant sur le fichier importé
     * @param userInfo UserInfo
     * @return String Le résultat de la vérification au format JSON
     */
    @Throws(JsonProcessingException::class)
    fun importCtpValidation(file: InputStream, userInfo: UserInfo): ImportCtpData {
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        val data = ImportCtpData()
        val resultatVerifications: MutableList<LigneImportCtpData> = mutableListOf()

        val workbook: Workbook
        // Gestion erreur fichier illisible
        try {
            workbook = WorkbookFactory.create(file)
            workbook.missingCellPolicy = Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        } catch (e: Exception) {
            resultatVerifications.add(
                buildGlobalErrorData(ErreurImportCtp.ERR_FICHIER_INNAC),
            )
            data.bilanVerifications = resultatVerifications
            return data
        }

        // Gestion erreur feuille n°2 (sur laquelle se trouvent les données) n'existe pas
        val sheet: Sheet
        try {
            sheet = workbook.getSheetAt(ONGLET_SAISIE_INDEX)
            if (ONGLET_SAISIE != sheet.sheetName) {
                throw Exception()
            }
        } catch (e: Exception) {
            resultatVerifications.add(
                buildGlobalErrorData(ErreurImportCtp.ERR_ONGLET_ABS),
            )
            data.bilanVerifications = resultatVerifications
            return data
        }

        // Lecture des valeurs
        var nbLigne: Int = NB_LIGNES_RESERVEES
        var estFini = false
        while (nbLigne < sheet.physicalNumberOfRows && !estFini) {
            var resultatVerification: LigneImportCtpData
            try {
                val r: Row = sheet.getRow(nbLigne)
                if (r.firstCellNum.toInt() == 0 && r.getCell(0) != null) {
                    // Evite de traiter la ligne "fantôme" détectée par la librairie à cause des combos des anomalies
                    resultatVerification = this.checkLineValidity(r, userInfo)
                    resultatVerification.numeroLigne = nbLigne + 1
                    resultatVerifications.add(resultatVerification)
                } else {
                    estFini = true
                }
            } catch (e: ImportCtpException) {
                resultatVerification = e.getData()
                resultatVerification.bilan = (e.getTypeErreur().libelleLong)
                resultatVerification.bilanStyle = e.getTypeErreur().gravite
                resultatVerification.numeroLigne = nbLigne + 1
                resultatVerifications.add(resultatVerification)
                resultatVerification.removeWarnings()
            }
            nbLigne++
        }
        data.bilanVerifications = resultatVerifications
        return data
    }

    /**
     * Retourne une valeur textuelle de la cellule même quand excel la formate sous forme numérique
     * (Ex : code Insee)
     *
     * @param c Cell
     * @return String la représentation en String de la valeur de la cellule
     */
    private fun getStringValueFromCell(c: Cell): String? {
        if (c.cellType === CellType.STRING) {
            return c.stringCellValue
        }
        if (c.cellType === CellType.NUMERIC) {
            return (c.numericCellValue.toInt()).toString()
        }
        return null
    }

    /**
     * Lit un nombre réel depuis une cellule. La valeur doit être au format décimal point ou virgule
     *
     * @param c La cellule contentant la donnée
     * @return La valeur de la coordonnée de type Double
     * @throws Exception La valeur ne respecte pas le format attendu
     */
    @Throws(Exception::class)
    fun getNumericValueFromCell(c: Cell): Double {
        if (c.cellType === CellType.NUMERIC) {
            return c.numericCellValue
        }

        if (c.cellType !== CellType.STRING) {
            throw Exception()
        }

        val strCell: String = c.stringCellValue.replace(",", ".")
        if (!strCell.matches("([+-]?\\d+\\.?\\d+)\\s*".toRegex())) {
            throw Exception()
        }
        return strCell.toDouble()
    }

    /**
     * Lors de l'import des visites CTP, vérifie la validité d'une ligne donnée
     *
     * @param row La ligne contenant toutes les cellules requises
     * @return DataImportCtp un POJO contenant les résultats de la validation pour cette ligne, à
     * sérialiser
     * @throws ImportCtpException En cas de donnée incorrecte, déclenche une exception gérée et
     * traitée par la fonction parente
     */
    private fun checkLineValidity(
        row: Row,
        userInfo: UserInfo,
    ): LigneImportCtpData {
        val data = LigneImportCtpData()
        val warnings: MutableList<String> = ArrayList()
        data.bilan = "CT Validé"
        data.bilanStyle = ErreurImportCtp.Gravite.OK

        val peiId = UUID.fromString(row.getCell(CellDefinition.ID_HYDRANT.index).stringCellValue)
        val codeInsee = this.getStringValueFromCell(row.getCell(CellDefinition.CODE_INSEE.index))
            ?: throw RemocraResponseException(ErrorType.IMPORT_CTP_CODE_INSEE_MANQUANT)
        val numeroInterne =
            row.getCell(CellDefinition.NUMERO_INTERNE.index).stringCellValue.toInt()

        data.codeInsee = codeInsee
        data.numeroInterne = numeroInterne

        // Vérification si le PEI renseigné correspond bien à celui en base
        if (!peiRepository.checkForImportCTP(peiId, numeroInterne, codeInsee)) {
            throw ImportCtpException(ErreurImportCtp.ERR_MAUVAIS_NUM_PEI, data)
        }

        // On vérifie si le PEI est bien dans la zone de compétence de l'utilisateur
        val pei = peiRepository.getGeometriePei(peiId = peiId)

        checkZoneCompetence(userInfo, listOf(pei))

        // Si la visite CTP n'est pas renseignée (si tous les champs composant les informations de la
        // visite sont vides)
        var ctpRenseigne = false
        for (i in DEBUT_CTP_INDEX until FIN_CTP_INDEX) {
            if (row.getCell(i) != null && row.getCell(i).cellType !== CellType.BLANK) {
                ctpRenseigne = true
            }
        }
        if (!ctpRenseigne) {
            // On passe par un throw car à ce stade on a déjà toutes les infos que l'on souhaite afficher
            // On n'a pas besoin de continuer les vérifications
            throw ImportCtpException(ErreurImportCtp.INFO_IGNORE, data)
        }

        // Vérifications au niveau de la date
        if (row.getCell(CTP_DATE_INDEX) == null || row.getCell(CTP_DATE_INDEX).cellType === CellType.BLANK
        ) {
            throw ImportCtpException(ErreurImportCtp.ERR_DATE_MANQ, data)
        }

        val instantCtp: ZonedDateTime
        try {
            instantCtp = dateUtils.getInstant(row.getCell(CTP_DATE_INDEX).localDateTimeCellValue)
        } catch (e: Exception) {
            throw ImportCtpException(ErreurImportCtp.ERR_FORMAT_DATE, data)
        }

        if (instantCtp.isAfter(dateUtils.now())) {
            throw ImportCtpException(ErreurImportCtp.ERR_DATE_POST, data)
        }

        var nbVisite: Int = visiteRepository.getNbVisitesCtrlAfter(peiId, instantCtp)
        if (nbVisite > 0) {
            warnings.add(ErreurImportCtp.WARN_DATE_ANTE.libelleLong)
        }

        // On vérifie que le PEI dispose de ses deux premières visites (réception et ROI) pour pouvoir
        // lui adjoindre une visite CTP
        nbVisite = visiteRepository.getCountVisite(peiId)
        if (nbVisite < 2) {
            throw ImportCtpException(ErreurImportCtp.ERR_VISITES_MANQUANTES, data)
        }

        // On vérifie s'il n'y pas de visite à la même date et heure
        nbVisite = visiteRepository.getNbVisitesEqInstant(peiId, instantCtp)
        if (nbVisite > 0) {
            throw ImportCtpException(ErreurImportCtp.ERR_VISITE_EXISTANTE, data)
        }

        data.dateCtp = dateUtils.formatDateOnly(instantCtp)

        val xlsAgent1: String
        if (row.getCell(CTP_AGENT1_INDEX) == null || row.getCell(CTP_AGENT1_INDEX).cellType === CellType.BLANK
        ) {
            throw ImportCtpException(ErreurImportCtp.ERR_AGENT1_ABS, data)
        } else {
            xlsAgent1 = row.getCell(CTP_AGENT1_INDEX).stringCellValue
        }

        var xlsDebit: Int? = null
        try {
            if (row.getCell(CTP_DEBIT_INDEX) != null && row.getCell(CTP_DEBIT_INDEX).cellType !== CellType.BLANK
            ) {
                xlsDebit = row.getCell(CTP_DEBIT_INDEX).numericCellValue.toInt()

                if ((row.getCell(CTP_DEBIT_INDEX).numericCellValue % 1).toInt() != 0) {
                    // Si on a réalisé une troncature lors de la lecture de la valeur
                    data.bilan = ErreurImportCtp.INFO_TRONC_DEBIT.libelleLong
                    data.bilanStyle = ErreurImportCtp.INFO_TRONC_DEBIT.gravite
                }
                if (xlsDebit < 0) {
                    throw Exception()
                }
            }
        } catch (e: Exception) {
            throw ImportCtpException(ErreurImportCtp.ERR_FORMAT_DEBIT, data)
        }

        var xlsPression: Double? = null
        try {
            if (row.getCell(CTP_PRESSION_INDEX) != null && row.getCell(CTP_PRESSION_INDEX).cellType !== CellType.BLANK
            ) {
                xlsPression = this.getNumericValueFromCell(row.getCell(CTP_PRESSION_INDEX))
                if (xlsPression < 0) {
                    throw Exception()
                }
            }
        } catch (e: Exception) {
            throw ImportCtpException(ErreurImportCtp.ERR_FORMAT_PRESS, data)
        }

        if (xlsPression != null && xlsPression > PRESSION_MAX) {
            throw ImportCtpException(ErreurImportCtp.ERR_PRESS_ELEVEE, data)
        }

        var warningDebitPression: ErreurImportCtp? = null
        if (xlsPression == null && xlsDebit == null) {
            warningDebitPression = ErreurImportCtp.WARN_DEB_PRESS_VIDE
        } else if (xlsPression == null) {
            warningDebitPression = ErreurImportCtp.WARN_PRESS_VIDE
        } else if (xlsDebit == null) {
            warningDebitPression = ErreurImportCtp.WARN_DEBIT_VIDE
        }

        if (warningDebitPression != null) {
            warnings.add(warningDebitPression.libelleLong)
        }

        var latitude: Double? = null
        var longitude: Double? = null

        // Si l'utilisateur a le droit de déplacer un PEI, on affiche un warning si la distance de
        // déplacement est supérieure à la distance renseignée dans les paramètres de l'application
        if (userInfo.isSuperAdmin || userInfo.droits.contains(Droit.IMPORT_CTP_PEI_DEPLACEMENT_U)) {
            try {
                latitude = this.getNumericValueFromCell(row.getCell(LATITUDE_INDEX))
                longitude = this.getNumericValueFromCell(row.getCell(LONGITUDE_INDEX))
            } catch (e: Exception) {
                throw ImportCtpException(ErreurImportCtp.ERR_COORD_GPS, data)
            }

            val newPeiPosition = GeometryFactory(PrecisionModel()).createPoint(Coordinate(latitude, longitude))
            val sourceCRS = CRS.decode("EPSG:${GlobalConstants.SRID_4326}")
            val targetCRS = CRS.decode(appSettings.epsg.name)
            val transform = CRS.findMathTransform(sourceCRS, targetCRS)
            val geometryProjectionTo = JTS.transform(newPeiPosition, transform)
                ?: throw IllegalArgumentException("Impossible de convertir la géometrie $newPeiPosition en ${appSettings.srid}")
            val distance = geometryProjectionTo.distance(pei)

            if (distance > parametresProvider.getParametreInt(ParametreEnum.PEI_DEPLACEMENT_DIST_WARN.name)!!) {
                warnings.add(ErreurImportCtp.WARN_DEPLACEMENT.libelleLong)
            }
        }

        // Vérifications anomalies
        val listVisiteAnomalieId: MutableSet<UUID> = mutableSetOf()

        // On récupère les identifiants des anomalies inscrites dans le fichier
        for (i in CTP_DEBUT_ANOMALIE_INDEX..CTP_FIN_ANOMALIE_INDEX) {
            if (row.getCell(i) != null && row.getCell(i).cellType !== CellType.BLANK && row.getCell(i).stringCellValue.trim().isNotEmpty()) {
                val xlsAnomalie: String = row.getCell(i).stringCellValue
                val codeAnomalie: String = xlsAnomalie.split(ANOMALIES_SEPARATEUR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }.uppercase(Locale.getDefault())

                val anomalie = dataCacheProvider.getAnomalies().entries.find { it.value.anomalieCode == codeAnomalie }?.value
                    ?: throw ImportCtpException(ErreurImportCtp.ERR_ANO_INCONNU, data)
                listVisiteAnomalieId.add(anomalie.anomalieId)
            }
        }

        // On récupère les anomalies de la visite précédente qui ne sont pas assignables dans un contexte de CTP.
        // Ça évite de cramer des anomalies que la saisie était incapable de retirer depuis le Front.
        val previousVisite = visiteRepository.getLastVisiteBefore(peiId = peiId, instant = instantCtp)
        val previousVisiteAnomalies = visiteRepository.getAnomaliesFromVisite(previousVisite.visiteId)
        val listAnomalieCtp = anomalieRepository.getAllAnomalieAssignable(peiId)
            .filter { it.isCTPAssignable }
            .map { it.anomalieId }
        val oldAnomaliesToReassign = previousVisiteAnomalies.filterNot { listAnomalieCtp.contains(it) }
        // On ajoute ces anomalies à la liste des anomalies associées à la visite.
        listVisiteAnomalieId.addAll(oldAnomaliesToReassign)

        val observation: String? =
            if ((row.lastCellNum - 1 >= FIN_CTP_INDEX) && row.getCell(CTP_OBSERVATIONS_INDEX) != null
            ) {
                row.getCell(CTP_OBSERVATIONS_INDEX).stringCellValue
            } else {
                null
            }

        // Ajout des données de la visite à ajouter aux informations JSON Ces données ont déjà été
        // vérifiées ici, il n'y a pas besoin de dupliquer les vérifications avant l'ajout en base
        data.dataVisite = LigneImportCtpVisiteData(
            importListeAnomalies = listVisiteAnomalieId,
            importDate = instantCtp,
            importPeiId = peiId,
            importAgent1 = xlsAgent1,
            importDebit = xlsDebit,
            importPression = xlsPression,
            importObservation = observation,
            importLatitude = latitude,
            importLongitude = longitude,
        )

        if (warnings.isNotEmpty()) {
            data.addAllWarnings(warnings)
            data.bilanStyle = ErreurImportCtp.Gravite.WARNING
        }
        return data
    }

    private enum class CellDefinition(val index: Int, definition: String, cellType: RemocraCellType) {
        ID_HYDRANT(0, "Identifiant du PEI", RemocraCellType.STRING),
        COMMUNE(1, "Commune - non utilisé", RemocraCellType.STRING),
        CODE_INSEE(2, "Code INSEE de la commune", RemocraCellType.STRING),
        NUMERO_INTERNE(3, "Numéro interne", RemocraCellType.INT),
    }

    private enum class RemocraCellType {
        STRING,
        INT,
    }

    fun importCtpEnregistrement(importCtpData: ImportCtpData, userInfo: UserInfo) {
        importCtpData.bilanVerifications?.forEach {
            if (it.dataVisite != null) {
                addVisiteFromImportCtp(it.dataVisite!!, userInfo)
            }
        }
    }

    private fun addVisiteFromImportCtp(visitesData: LigneImportCtpVisiteData, userInfo: UserInfo) {
        transactionManager.transactionResult {
            createVisiteUseCase.execute(
                userInfo,
                VisiteData(
                    visiteId = UUID.randomUUID(),
                    visitePeiId = visitesData.importPeiId,
                    visiteDate = visitesData.importDate,
                    visiteTypeVisite = TypeVisite.CTP,
                    visiteAgent1 = visitesData.importAgent1,
                    visiteAgent2 = null,
                    visiteObservation = visitesData.importObservation,
                    listeAnomalie = visitesData.importListeAnomalies.toList(),
                    isCtrlDebitPression = visitesData.importDebit != null || visitesData.importPression != null,
                    ctrlDebitPression =
                    CreationVisiteCtrl(
                        ctrlDebit = visitesData.importDebit,
                        ctrlPression = visitesData.importPression?.toBigDecimal(),
                        ctrlPressionDyn = null,
                    ),
                    isFromImportCtp = true,
                ),
                transactionManager,
            )

            // L'export CTP fourni les coordonnées en EPSG:4326,
            // on s'attend donc à avoir en retour des coordonnées en 4326, d'où la GlobalConstant
            if (visitesData.importLatitude != null && visitesData.importLongitude != null && userInfo.droits.contains(Droit.IMPORT_CTP_PEI_DEPLACEMENT_U)) {
                updatePeiUseCase.execute(
                    userInfo,
                    movePeiUseCase.execute(
                        // Latitude = Y ; Longitude = X
                        CoordonneesXYSrid(
                            visitesData.importLatitude!!,
                            visitesData.importLongitude!!,
                            GlobalConstants.SRID_4326,
                        ),
                        visitesData.importPeiId,
                    ),
                    transactionManager,
                )
            }
        }
    }
}
