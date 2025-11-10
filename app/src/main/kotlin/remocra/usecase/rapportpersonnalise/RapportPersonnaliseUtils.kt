import jakarta.inject.Inject
import org.jooq.Record
import org.jooq.Result
import remocra.app.AppSettings
import remocra.auth.WrappedUserInfo
import remocra.data.GenererRapportPersonnaliseData
import remocra.db.RapportPersonnaliseRepository
import remocra.utils.RequestUtils
import remocra.utils.canParseParam
import remocra.utils.parseParam

/**
 * Utilitaires pour les rapports personnalisés
 */
class RapportPersonnaliseUtils {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    lateinit var requestUtils: RequestUtils

    @Inject lateinit var appSettings: AppSettings

    /**
     *  Construit les données du rapport personnalisé en fonction des paramètres fournis
     */
    fun buildRapportPersonnaliseData(genererRapportPersonnaliseData: GenererRapportPersonnaliseData, userInfo: WrappedUserInfo): Result<Record> {
        // On va chercher la requête du rapport
        var requete = rapportPersonnaliseRepository.getSqlRequete(genererRapportPersonnaliseData.rapportPersonnaliseId)

        genererRapportPersonnaliseData.listeParametre.forEach { param ->
            val code = param.rapportPersonnaliseParametreCode
            val value = param.value.orEmpty()

            requete = when {
                value.isBlank() -> requete.replace(code, "null")

                canParseParam(value) -> {
                    val parsed = parseParam(value)
                    requete.replace("'$code'", "ST_Transform(ST_GeomFromText('${parsed.wkt}', ${parsed.srid}), ${appSettings.srid})")
                }

                else -> requete.replace(code, value)
            }
        }

        // On remplace les variables utilisateur de la requête par les données userinfo
        requete = requestUtils.replaceGlobalParameters(userInfo, requete)

        return rapportPersonnaliseRepository.executeSqlRapport(requete)
    }
}
