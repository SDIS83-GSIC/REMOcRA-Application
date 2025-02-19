package remocra.usecase.courrier

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import jakarta.ws.rs.ForbiddenException
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperRunManager
import remocra.GlobalConstants
import remocra.auth.UserInfo
import remocra.data.courrier.parametres.AbstractCourrierParametresData
import remocra.data.courrier.template.AbstractCourrierData
import remocra.db.TransactionManager
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

/**
 * Cette classe permet de demander la génération d'un courrier en vérifiant les droits de l'utilisateur.
 */
abstract class AbstractCourrierGenerator<T : AbstractCourrierParametresData> {

    @Inject
    lateinit var transactionManager: TransactionManager

    @Inject
    lateinit var objectMapper: ObjectMapper

    /**
     * Vérifie les droits de l'utilisateur, et déclenche une [ForbiddenException] si l'utilisateur
     * n'est pas dans le bon profil droit
     */
    protected abstract fun checkProfilDroit(userInfo: UserInfo)

    /**
     * Permet d'aller chercher tous les paramètres d'un courrier.
     * Elle renvoie un objet de type AbstractCourrierData
     */
    protected abstract fun execute(element: T, userInfo: UserInfo): AbstractCourrierData

    /** Fonction commune pour la génération de tous les courriers */
    fun execute(
        courrierParametreData: T,
        userInfo: UserInfo?,
    ): ByteArray? {
        if (userInfo == null) {
            throw ForbiddenException("Vous ne possédez pas les droits pour générer ce courrier")
        }
        checkProfilDroit(userInfo)
        var courrierData: AbstractCourrierData? = null

        transactionManager.transactionResult {
            courrierData = execute(courrierParametreData, userInfo)
        }

        if (courrierData == null) {
            throw IllegalArgumentException("Impossible de récupérer les données pour remplir le template")
        }

        val location = courrierData.courrierPath
        val courrier = JasperCompileManager.compileReport(location)

        val mapParameters = toMap(courrierData).toMutableMap()

        // On compile ensuite les sous rapports
        if (courrierData.courrierSubReport != null) {
            val listSubReport = objectMapper.readValue<List<SubReport>>(courrierData.courrierSubReport!!.data())

            listSubReport.forEach {
                mapParameters[it.nom] = JasperCompileManager.compileReport(GlobalConstants.DOSSIER_MODELES_COURRIERS + it.chemin)
            }
        }

        return JasperRunManager.runReportToPdf(
            courrier,
            mapParameters,
            JREmptyDataSource(),
        )
    }

    /**
     * Permet de transformer un objet en map
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> toMap(obj: T): Map<String, Any?> {
        return (obj::class as KClass<T>).memberProperties.associate { prop ->
            prop.name to prop.get(obj)?.let { value ->
                if (value::class.isData) {
                    toMap(value)
                } else {
                    value
                }
            }
        }
    }

    data class SubReport(
        val nom: String,
        val chemin: String,
    )
}
