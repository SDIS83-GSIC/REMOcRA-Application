package remocra.usecase.courrier

import com.google.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.UserInfo
import remocra.data.courrier.form.NomValue
import remocra.data.courrier.form.ParametreCourrierInput
import remocra.db.ModeleCourrierRepository
import remocra.usecase.document.DocumentUtils
import remocra.utils.DateUtils
import java.util.UUID

class CourrierGenerator {

    @Inject
    lateinit var modeleCourrierRepository: ModeleCourrierRepository

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var dateUtils: DateUtils

    fun execute(parametreCourrierInput: ParametreCourrierInput, userInfo: UserInfo?, uriBuilder: UriBuilder): UrlCourrier? {
        // TODO
        return null
    }

    data class UrlCourrier(
        val url: String,
    )

    private fun getValue(listParametres: List<NomValue>?, nomParametre: String) =
        listParametres?.firstOrNull { it.nom == nomParametre }?.valeur

    private fun getValueUUID(listParametres: List<NomValue>?, nomParametre: String): UUID? {
        val value = getValue(listParametres, nomParametre)
        if (value != null) {
            return UUID.fromString(value)
        }
        return null
    }
}
