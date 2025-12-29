package remocra.utils

import jakarta.inject.Inject
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.IdLibelleRapportPersonnalise
import remocra.data.ModeleCourrierData
import remocra.data.RapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.RapportPersonnaliseRepository
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.exception.RemocraResponseException
import java.util.UUID

class RequeteSqlUtils {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var dateUtils: DateUtils

    @Inject
    private lateinit var requestUtils: RequestUtils

    @Inject
    private lateinit var appSettings: AppSettings

    private fun testParametreRequeteSql(userInfo: WrappedUserInfo, parametreRequete: RapportCourrierParametreData): List<IdLibelleRapportPersonnalise> {
        try {
            val requeteModifiee = requestUtils.replaceGlobalParameters(userInfo, parametreRequete.rapportCourrierParametreSourceSql!!)
            return rapportPersonnaliseRepository.executeSqlParametre(requeteModifiee)
        } catch (e: Exception) {
            throw RemocraResponseException(
                ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_PARAMETRE_INVALID,
                "(paramètre :  ${parametreRequete.rapportCourrierParametreCode}) : ${e.message}",
            )
        }
    }

    fun checkContraintes(userInfo: WrappedUserInfo, element: RapportPersonnaliseData) {
        checkContraintes(
            userInfo,
            RapportCourrierData(
                rapportCourrierId = element.rapportPersonnaliseId,
                rapportCourrierActif = element.rapportPersonnaliseActif,
                rapportCourrierCode = element.rapportPersonnaliseCode,
                rapportCourrierLibelle = element.rapportPersonnaliseLibelle,
                rapportCourrierChampGeometrie = element.rapportPersonnaliseChampGeometrie,
                rapportCourrierDescription = element.rapportPersonnaliseDescription,
                rapportCourrierSourceSql = element.rapportPersonnaliseSourceSql,
                rapportCourrierModule = element.rapportPersonnaliseModule,
                listeGroupeFonctionnalitesId = element.listeGroupeFonctionnalitesId,
                listeRapportCourrierParametre = element.listeRapportPersonnaliseParametre.map {
                    RapportCourrierParametreData(
                        rapportCourrierParametreId = it.rapportPersonnaliseParametreId,
                        rapportCourrierParametreCode = it.rapportPersonnaliseParametreCode,
                        rapportCourrierParametreLibelle = it.rapportPersonnaliseParametreLibelle,
                        rapportCourrierParametreSourceSql = it.rapportPersonnaliseParametreSourceSql,
                        rapportCourrierParametreDescription = it.rapportPersonnaliseParametreDescription,
                        rapportCourrierParametreSourceSqlId = it.rapportPersonnaliseParametreSourceSqlId,
                        rapportCourrierParametreSourceSqlLibelle = it.rapportPersonnaliseParametreSourceSqlLibelle,
                        rapportCourrierParametreValeurDefaut = it.rapportPersonnaliseParametreValeurDefaut,
                        rapportCourrierParametreIsRequired = it.rapportPersonnaliseParametreIsRequired,
                        rapportCourrierParametreType = it.rapportPersonnaliseParametreType,
                        rapportCourrierParametreOrdre = it.rapportPersonnaliseParametreOrdre,
                    )
                },
            ),
        )
    }

    fun checkContraintes(userInfo: WrappedUserInfo, element: ModeleCourrierData) {
        checkContraintes(
            userInfo,
            RapportCourrierData(
                rapportCourrierId = element.modeleCourrierId!!,
                rapportCourrierActif = element.modeleCourrierActif,
                rapportCourrierCode = element.modeleCourrierCode,
                rapportCourrierLibelle = element.modeleCourrierLibelle,
                rapportCourrierChampGeometrie = null,
                rapportCourrierDescription = element.modeleCourrierDescription,
                rapportCourrierSourceSql = element.modeleCourrierSourceSql,
                rapportCourrierModule = element.modeleCourrierModule,
                listeGroupeFonctionnalitesId = element.listeGroupeFonctionnalitesId,
                listeRapportCourrierParametre = element.listeModeleCourrierParametre.map {
                    RapportCourrierParametreData(
                        rapportCourrierParametreId = it.modeleCourrierParametreId,
                        rapportCourrierParametreCode = it.modeleCourrierParametreCode,
                        rapportCourrierParametreLibelle = it.modeleCourrierParametreLibelle,
                        rapportCourrierParametreSourceSql = it.modeleCourrierParametreSourceSql,
                        rapportCourrierParametreDescription = it.modeleCourrierParametreDescription,
                        rapportCourrierParametreSourceSqlId = it.modeleCourrierParametreSourceSqlId,
                        rapportCourrierParametreSourceSqlLibelle = it.modeleCourrierParametreSourceSqlLibelle,
                        rapportCourrierParametreValeurDefaut = it.modeleCourrierParametreValeurDefaut,
                        rapportCourrierParametreIsRequired = it.modeleCourrierParametreIsRequired,
                        rapportCourrierParametreType = it.modeleCourrierParametreType,
                        rapportCourrierParametreOrdre = it.modeleCourrierParametreOrdre,
                    )
                },
            ),
        )
    }

    fun containsWordNotInSingleQuote(text: String, word: String): Boolean {
        val regex = """(?<!')\b$word\b(?!')""".toRegex(RegexOption.IGNORE_CASE)
        return regex.containsMatchIn(text)
    }

    private fun checkContraintes(userInfo: WrappedUserInfo, element: RapportCourrierData) {
        // Aucun paramètre ne doivent avoir le même code
        if (element.listeRapportCourrierParametre.map { it.rapportCourrierParametreCode }.distinct().size != element.listeRapportCourrierParametre.size) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_PARAMETRE_CODE_UNIQUE)
        }

        // TODO vérifier qu'on n'est pas injection ?
        if (element.rapportCourrierSourceSql.contains("CREATE", true) ||
            containsWordNotInSingleQuote(element.rapportCourrierSourceSql, "UPDATE") ||
            element.rapportCourrierSourceSql.contains("DROP", true) ||
            containsWordNotInSingleQuote(element.rapportCourrierSourceSql, "DELETE") ||
            element.rapportCourrierSourceSql.contains("TRUNCATE", true)
        ) {
            throw RemocraResponseException(
                ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID,
                "Ne doit pas contenir de CREATE, UPDATE, DROP, DELETE ou TRUNCATE",
            )
        }

        if (!element.rapportCourrierSourceSql.startsWith("SELECT", ignoreCase = true) &&
            !element.rapportCourrierSourceSql.startsWith("WITH", ignoreCase = true)
        ) {
            throw RemocraResponseException(
                ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID,
                "doit commencer par un 'SELECT' ou un 'WITH'",
            )
        }

        var requete = element.rapportCourrierSourceSql

        // On doit aussi remplacer les paramètres pour pouvoir vérifier que la requête est correcte
        element.listeRapportCourrierParametre.forEach {
            when (it.rapportCourrierParametreType) {
                TypeParametreRapportCourrier.CHECKBOX_INPUT ->
                    requete = requete.replace(it.rapportCourrierParametreCode, "true")
                TypeParametreRapportCourrier.DATE_INPUT ->
                    requete = requete.replace(it.rapportCourrierParametreCode, it.rapportCourrierParametreValeurDefaut.let { param -> if (param.isNullOrEmpty()) dateUtils.format(dateUtils.now()) else param })
                TypeParametreRapportCourrier.NUMBER_INPUT ->
                    requete = requete.replace(it.rapportCourrierParametreCode, it.rapportCourrierParametreValeurDefaut ?: "10")
                TypeParametreRapportCourrier.SELECT_INPUT ->
                    requete = requete.replace(it.rapportCourrierParametreCode, testParametreRequeteSql(userInfo, it).firstOrNull()?.id ?: "null")
                TypeParametreRapportCourrier.TEXT_INPUT ->
                    requete = requete.replace(it.rapportCourrierParametreCode, it.rapportCourrierParametreValeurDefaut ?: "")
                TypeParametreRapportCourrier.POINT ->
                    requete = requete.replace("'${it.rapportCourrierParametreCode}'", "ST_SetSRID('POINT EMPTY'::geometry, ${appSettings.srid})")
                TypeParametreRapportCourrier.POLYGON ->
                    requete = requete.replace("'${it.rapportCourrierParametreCode}'", "ST_SetSRID('POLYGON EMPTY'::geometry, ${appSettings.srid}")
                TypeParametreRapportCourrier.LINESTRING ->
                    requete = requete.replace("'${it.rapportCourrierParametreCode}'", "ST_SetSRID('LINESTRING EMPTY'::geometry, ${appSettings.srid}")
            }
        }

        // On vérifie ensuite la requête globale
        try {
            val requeteModifiee = requestUtils.replaceGlobalParameters(userInfo, requete)
            rapportPersonnaliseRepository.executeSqlRapport(requeteModifiee)
        } catch (e: Exception) {
            throw RemocraResponseException(ErrorType.ADMIN_RAPPORT_PERSO_REQUETE_INVALID, e.message)
        }
    }

    data class RapportCourrierData(
        val rapportCourrierId: UUID,
        val rapportCourrierActif: Boolean,
        val rapportCourrierCode: String,
        val rapportCourrierLibelle: String,
        val rapportCourrierChampGeometrie: String?,
        val rapportCourrierDescription: String?,
        val rapportCourrierSourceSql: String,
        val rapportCourrierModule: TypeModuleRapportCourrier,
        val listeGroupeFonctionnalitesId: Collection<UUID>,
        val listeRapportCourrierParametre: Collection<RapportCourrierParametreData>,
    )

    data class RapportCourrierParametreData(
        val rapportCourrierParametreId: UUID = UUID.randomUUID(),
        val rapportCourrierParametreCode: String,
        val rapportCourrierParametreLibelle: String,
        val rapportCourrierParametreSourceSql: String?,
        val rapportCourrierParametreDescription: String?,
        val rapportCourrierParametreSourceSqlId: String?,
        val rapportCourrierParametreSourceSqlLibelle: String?,
        val rapportCourrierParametreValeurDefaut: String?,
        val rapportCourrierParametreIsRequired: Boolean,
        val rapportCourrierParametreType: TypeParametreRapportCourrier,
        val rapportCourrierParametreOrdre: Int,
    )
}
