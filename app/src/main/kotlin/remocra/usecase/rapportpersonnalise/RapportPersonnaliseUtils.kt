import jakarta.inject.Inject
import org.jooq.Record
import org.jooq.Result
import remocra.auth.WrappedUserInfo
import remocra.data.GenererRapportPersonnaliseData
import remocra.db.RapportPersonnaliseRepository
import remocra.utils.RequestUtils

/**
 * Utilitaires pour les rapports personnalisés
 */
class RapportPersonnaliseUtils {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    lateinit var requestUtils: RequestUtils

    /**
     *  Construit les données du rapport personnalisé en fonction des paramètres fournis
     */
    fun buildRapportPersonnaliseData(genererRapportPersonnaliseData: GenererRapportPersonnaliseData, userInfo: WrappedUserInfo): Result<Record> {
        // On va chercher la requête du rapport
        var requete = rapportPersonnaliseRepository.getSqlRequete(genererRapportPersonnaliseData.rapportPersonnaliseId)

        // On remplace avec les données paramètres fournies
        genererRapportPersonnaliseData.listeParametre.forEach {
            requete = requete.replace(
                it.rapportPersonnaliseParametreCode,
                it.value?.takeIf { it.isNotBlank() } ?: "null",
            )
        }

        // On remplace les variables utilisateur de la requête par les données userinfo
        requete = requestUtils.replaceGlobalParameters(userInfo, requete)

        return rapportPersonnaliseRepository.executeSqlRapport(requete)
    }
}
