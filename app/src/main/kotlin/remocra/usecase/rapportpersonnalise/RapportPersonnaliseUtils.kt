import jakarta.inject.Inject
import org.jooq.Record
import org.jooq.Result
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.GenererRapportPersonnaliseData
import remocra.db.RapportPersonnaliseRepository
import remocra.db.rawsql.RawSqlQueryBuilder.Companion.toRawSqlQueryBuilder
import remocra.utils.RequestUtils
import remocra.utils.canParseParam
import remocra.utils.parseParam

/**
 * Utilitaires pour les rapports personnalisés
 */
class RapportPersonnaliseUtils
@Inject
constructor(
    private val rapportPersonnaliseRepository: RapportPersonnaliseRepository,
    private val requestUtils: RequestUtils,
    private val appSettings: AppSettings,
) {

    companion object {
        private const val PARSED_WKT_PARAM_NAME = "__PARSED_WKT__"
        private const val PARSED_SRID_PARAM_NAME = "__PARSED_SRID__"
        private const val APP_SETTING_SRID_PARAM_NAME = "__APP_SETTING_SRID__"
    }

    /**
     *  Construit les données du rapport personnalisé en fonction des paramètres fournis
     */
    fun buildRapportPersonnaliseData(genererRapportPersonnaliseData: GenererRapportPersonnaliseData, userInfo: WrappedUserInfo): Result<Record> {
        // On va chercher la requête du rapport
        val requete = rapportPersonnaliseRepository.getSqlRequete(genererRapportPersonnaliseData.rapportPersonnaliseId)
            .toRawSqlQueryBuilder()

        genererRapportPersonnaliseData.listeParametre.forEach { param ->
            val code = param.rapportPersonnaliseParametreCode
            val value = param.value.orEmpty()

            when {
                value.isBlank() -> requete withBindParam (code to "null")

                canParseParam(value) -> {
                    val parsed = parseParam(value)
                    requete withRawReplace (
                        Regex("'$code'")
                            to "ST_Transform(ST_GeomFromText($PARSED_WKT_PARAM_NAME, $PARSED_SRID_PARAM_NAME), $APP_SETTING_SRID_PARAM_NAME)"
                        )
                    requete withBindParam (PARSED_WKT_PARAM_NAME to parsed.wkt)
                    requete withBindParam (PARSED_SRID_PARAM_NAME to parsed.srid)
                    requete withBindParam (APP_SETTING_SRID_PARAM_NAME to appSettings.srid)
                }

                else -> requete withBindParam (code to value)
            }
        }

        // On remplace les variables utilisateur de la requête par les données userinfo
        requestUtils.apply {
            requete.replaceGlobalParameters(userInfo)
        }

        return rapportPersonnaliseRepository.executeSqlRapport(requete.build())
    }
}
