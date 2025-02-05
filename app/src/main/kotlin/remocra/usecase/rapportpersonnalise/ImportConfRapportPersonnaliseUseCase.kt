package remocra.usecase.rapportpersonnalise

import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import remocra.auth.UserInfo
import remocra.csv.CsvReader
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.RapportPersonnaliseRepository
import remocra.db.TransactionManager
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnaliseParametre
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportConfRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var rappportPersonnaliseUtils: RapportPersonnaliseUtils

    @Inject
    private lateinit var csvReader: CsvReader

    @Inject
    private lateinit var transactionManager: TransactionManager

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userInfo: UserInfo?, zip: InputStream) {
        try {
            var listeRapportPersonnalise: Collection<ExportConfRapportPersonnaliseUseCase.ImportExportRapportPersonnalise>? = null
            var listeRapportPersonnaliseParametre: Collection<ExportConfRapportPersonnaliseUseCase.ImportExportRapportPersonnaliseParametre>? = null

            // On dézippe et on récupère les informations
            ZipInputStream(zip).use { zipInputStream ->
                var zipEntry: ZipEntry? = zipInputStream.nextEntry
                while (zipEntry != null) {
                    when (zipEntry.name) {
                        ExportConfRapportPersonnaliseUseCase.NOM_FICHIER_RAPPORT_PERSO ->
                            listeRapportPersonnalise =
                                csvReader.readCsvFile<ExportConfRapportPersonnaliseUseCase.ImportExportRapportPersonnalise>(
                                    ByteArrayInputStream(zipInputStream.readBytes()),
                                )

                        ExportConfRapportPersonnaliseUseCase.NOM_FICHIER_RAPPORT_PERSO_PARAM ->
                            listeRapportPersonnaliseParametre = csvReader.readCsvFile<ExportConfRapportPersonnaliseUseCase.ImportExportRapportPersonnaliseParametre>(
                                ByteArrayInputStream(zipInputStream.readBytes()),
                            )

                        else -> logger.error("Fichier ${zipEntry.name} non reconnu.")
                    }

                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }

            // Si on n'a pas de rapport personnalisé, on plante
            if (listeRapportPersonnalise.isNullOrEmpty()) {
                logger.error("Fichier ${ExportConfRapportPersonnaliseUseCase.NOM_FICHIER_RAPPORT_PERSO} inexistant.")
                throw IllegalArgumentException("Fichier ${ExportConfRapportPersonnaliseUseCase.NOM_FICHIER_RAPPORT_PERSO} inexistant.")
            }

            val rapportPersonnaliseId = UUID.randomUUID()

            transactionManager.transactionResult {
                try {
                    val rapportPersonnalise = listeRapportPersonnalise!!.first()
                    rappportPersonnaliseUtils.checkContraintes(
                        userInfo,
                        RapportPersonnaliseData(
                            rapportPersonnaliseId = rapportPersonnaliseId,
                            rapportPersonnaliseActif = rapportPersonnalise.rapportPersonnaliseActif,
                            rapportPersonnaliseCode = rapportPersonnalise.rapportPersonnaliseCode,
                            rapportPersonnaliseLibelle = rapportPersonnalise.rapportPersonnaliseLibelle,
                            rapportPersonnaliseChampGeometrie = rapportPersonnalise.rapportPersonnaliseChampGeometrie.takeUnless { it.isNullOrEmpty() },
                            rapportPersonnaliseDescription = rapportPersonnalise.rapportPersonnaliseDescription.takeUnless { it.isNullOrEmpty() },
                            rapportPersonnaliseSourceSql = rapportPersonnalise.rapportPersonnaliseSourceSql,
                            rapportPersonnaliseModule = TypeModuleRapportCourrier.valueOf(rapportPersonnalise.rapportPersonnaliseModule.name),
                            listeProfilDroitId = listOf(),
                            listeRapportPersonnaliseParametre = listeRapportPersonnaliseParametre
                                ?.map {
                                    RapportPersonnaliseParametreData(
                                        rapportPersonnaliseParametreId = UUID.randomUUID(),
                                        rapportPersonnaliseParametreCode = it.rapportPersonnaliseParametreCode,
                                        rapportPersonnaliseParametreLibelle = it.rapportPersonnaliseParametreLibelle,
                                        rapportPersonnaliseParametreSourceSql = it.rapportPersonnaliseParametreSourceSql.takeUnless { it.isNullOrEmpty() },
                                        rapportPersonnaliseParametreDescription = it.rapportPersonnaliseParametreDescription.takeUnless { it.isNullOrEmpty() },
                                        rapportPersonnaliseParametreSourceSqlId = it.rapportPersonnaliseParametreSourceSqlId.takeUnless { it.isNullOrEmpty() },
                                        rapportPersonnaliseParametreSourceSqlLibelle = it.rapportPersonnaliseParametreSourceSqlLibelle.takeUnless { it.isNullOrEmpty() },
                                        rapportPersonnaliseParametreValeurDefaut = null,
                                        rapportPersonnaliseParametreIsRequired = it.rapportPersonnaliseParametreIsRequired,
                                        rapportPersonnaliseParametreType = it.rapportPersonnaliseParametreType,
                                        rapportPersonnaliseParametreOrdre = it.rapportPersonnaliseParametreOrdre,
                                    )
                                } ?: listOf(),
                        ),
                    )

                    // On a un seul rapport à insérer
                    rapportPersonnaliseRepository.insertRapportPersonnalise(
                        RapportPersonnalise(
                            rapportPersonnaliseId = rapportPersonnaliseId,
                            rapportPersonnaliseActif = rapportPersonnalise.rapportPersonnaliseActif,
                            rapportPersonnaliseCode = rapportPersonnalise.rapportPersonnaliseCode,
                            rapportPersonnaliseLibelle = rapportPersonnalise.rapportPersonnaliseLibelle,
                            rapportPersonnaliseProtected = false,
                            rapportPersonnaliseChampGeometrie = rapportPersonnalise.rapportPersonnaliseChampGeometrie.takeUnless { it.isNullOrEmpty() },
                            rapportPersonnaliseDescription = rapportPersonnalise.rapportPersonnaliseDescription.takeUnless { it.isNullOrEmpty() },
                            rapportPersonnaliseSourceSql = rapportPersonnalise.rapportPersonnaliseSourceSql,
                            rapportPersonnaliseModule = rapportPersonnalise.rapportPersonnaliseModule,
                        ),
                    )

                    // Puis on s'occupe des paramètres
                    listeRapportPersonnaliseParametre?.forEach { param ->
                        rapportPersonnaliseRepository.upsertRapportPersonnaliseParametre(
                            RapportPersonnaliseParametre(
                                rapportPersonnaliseParametreId = UUID.randomUUID(),
                                rapportPersonnaliseParametreRapportPersonnaliseId = rapportPersonnaliseId,
                                rapportPersonnaliseParametreCode = param.rapportPersonnaliseParametreCode,
                                rapportPersonnaliseParametreLibelle = param.rapportPersonnaliseParametreLibelle,
                                rapportPersonnaliseParametreSourceSql = param.rapportPersonnaliseParametreSourceSql,
                                rapportPersonnaliseParametreDescription = param.rapportPersonnaliseParametreDescription.takeUnless { it.isNullOrEmpty() },
                                rapportPersonnaliseParametreSourceSqlId = param.rapportPersonnaliseParametreSourceSqlId.takeUnless { it.isNullOrEmpty() },
                                rapportPersonnaliseParametreSourceSqlLibelle = param.rapportPersonnaliseParametreSourceSqlLibelle.takeUnless { it.isNullOrEmpty() },
                                rapportPersonnaliseParametreValeurDefaut = null,
                                rapportPersonnaliseParametreIsRequired = param.rapportPersonnaliseParametreIsRequired,
                                rapportPersonnaliseParametreType = param.rapportPersonnaliseParametreType,
                                rapportPersonnaliseParametreOrdre = param.rapportPersonnaliseParametreOrdre,
                            ),
                        )
                    }

                    logger.info("Import effectué.")
                } catch (rre: RemocraResponseException) {
                    throw IllegalArgumentException(rre.message)
                } catch (e: Exception) {
                    throw IllegalArgumentException(e.message)
                }
            }
        } catch (e: Exception) {
            logger.error("Échec de l'import : ${e.message}")
            throw IllegalArgumentException("Échec de l'import : ${e.message}")
        }
    }
}
