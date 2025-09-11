package remocra.usecase.rapportpersonnalise

import RapportPersonnaliseUtils
import jakarta.inject.Inject
import org.jooq.Record
import remocra.auth.WrappedUserInfo
import remocra.data.GenererRapportPersonnaliseData
import remocra.data.enums.ErrorType
import remocra.db.RapportPersonnaliseRepository
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GenereRapportPersonnaliseUseCase : AbstractUseCase() {

    @Inject
    private lateinit var rapportPersonnaliseRepository: RapportPersonnaliseRepository

    @Inject
    private lateinit var rapportPersonnaliseUtils: RapportPersonnaliseUtils

    companion object {
        private const val FIELD_GEOMETRIE = "geometrie"
    }

    private fun checkGroupeFonctionnalites(userInfo: WrappedUserInfo, rapportPersonnaliseId: UUID) {
        if (userInfo.isSuperAdmin) {
            return
        }

        if (!rapportPersonnaliseRepository.checkDroitRapportPersonnalise(userInfo.utilisateurId!!, rapportPersonnaliseId)) {
            throw RemocraResponseException(ErrorType.RAPPORT_PERSO_FORBIDDEN)
        }
    }

    fun execute(userInfo: WrappedUserInfo, genererRapportPersonnaliseData: GenererRapportPersonnaliseData): RapportPersonnaliseTableau? {
        checkGroupeFonctionnalites(userInfo, genererRapportPersonnaliseData.rapportPersonnaliseId)

        return infosForTableRapportPerso(rapportPersonnaliseUtils.buildRapportPersonnaliseData(genererRapportPersonnaliseData, userInfo))
    }

    private fun infosForTableRapportPerso(data: org.jooq.Result<Record>?): RapportPersonnaliseTableau? {
        // Extraction des noms de champs
        if (data.isNullOrEmpty()) {
            return null
        }
        val fields = data[0].fields().map { it.name } as? Collection<String> ?: throw IllegalArgumentException("Erreur lors de la récupération des champs")
        return RapportPersonnaliseTableau(
            headers = fields,
            values = data.map { it.intoList() },
            // On oblige que le champ géométrie s'appelle "geometrie"
            geometries = data.map { it.field(FIELD_GEOMETRIE)?.getValue(it) as String? }.filterNotNull(),
        )
    }

    /**
     * @property header : liste des header du tableau
     * @property values : liste des valeurs
     * @property geometries : liste des géoémtries
     * Par exemple
     *      "headers": [
     *         "diametre_id",
     *         "diametre_actif",
     *         "diametre_code",
     *         "diametre_libelle",
     *         "diametre_protected"
     *     ],
     *     "values": [
     *         [
     *             "7bb65a21-3831-4cf2-83db-b1831cc24162",
     *             true,
     *             "DIAM70",
     *             "70",
     *             true
     *         ],
     *     "geometries": [
     *         [
     *             "POINT(1 2)",
     *             "POINT(1 3)",
     *         ],
     */
    data class RapportPersonnaliseTableau(
        // liste des header du tableau
        val headers: Collection<String>,
        // Valeurs
        val values: Collection<Collection<Any>>?,
        // Liste des géométries
        val geometries: Collection<String>?,
    )
}
