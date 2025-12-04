package remocra.usecase.risque

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.json.XML
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.risque.ImportRisqueExpressData
import remocra.db.RisqueExpressRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils
import java.util.UUID

class ImportRisqueExpressUseCase : AbstractCUDUseCase<ImportRisqueExpressData>(TypeOperation.INSERT) {

    @Inject
    lateinit var documentUtils: DocumentUtils

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var appSettings: AppSettings

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var risqueExpressRepository: RisqueExpressRepository

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RISQUE_EXPRESS_A)) {
            throw RemocraResponseException(ErrorType.RISQUE_FORBIDDEN_A)
        }
    }

    override fun postEvent(element: ImportRisqueExpressData, userInfo: WrappedUserInfo) {
        if (element.risqueId != null) {
            val risque = risqueExpressRepository.getById(element.risqueId)
            eventBus.post(
                TracabiliteEvent(
                    pojo = risque,
                    pojoId = element.risqueId,
                    typeOperation = typeOperation,
                    typeObjet = TypeObjet.RISQUE_EXPRESS,
                    auteurTracabilite = userInfo.getInfosTracabilite(),
                    date = dateUtils.now(),
                ),
            )
        }
    }

    override fun execute(userInfo: WrappedUserInfo, element: ImportRisqueExpressData): ImportRisqueExpressData {
        if (element.fileRisqueExpress != null) {
            val uuid = UUID.randomUUID()
            val xmlString = element.fileRisqueExpress.bufferedReader().use { it.readText() }
            val jsonString = XML.toJSONObject(xmlString).toString()

            risqueExpressRepository.insert(
                id = uuid,
                libelle = element.risqueLibelle ?: "Risque sans nom",
                geometries = jsonString,
            )
            return element.copy(risqueId = uuid, fileRisqueExpress = null)
        }

        return element.copy(fileRisqueExpress = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ImportRisqueExpressData) {
        // no-op
    }
}
