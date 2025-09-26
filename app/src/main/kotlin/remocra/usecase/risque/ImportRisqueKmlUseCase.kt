package remocra.usecase.risque

import com.google.inject.Inject
import remocra.app.AppSettings
import remocra.app.DataCacheProvider
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.data.risque.ImportRisqueKmlData
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.document.DocumentUtils

class ImportRisqueKmlUseCase : AbstractCUDUseCase<ImportRisqueKmlData>(TypeOperation.UPDATE) {

    @Inject lateinit var documentUtils: DocumentUtils

    @Inject lateinit var dataCacheProvider: DataCacheProvider

    @Inject lateinit var appSettings: AppSettings

    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.RISQUE_KML_A)) {
            throw RemocraResponseException(ErrorType.RISQUE_FORBIDDEN_A)
        }
    }

    override fun postEvent(element: ImportRisqueKmlData, userInfo: WrappedUserInfo) {
        // TODO
    }

    override fun execute(userInfo: WrappedUserInfo, element: ImportRisqueKmlData): ImportRisqueKmlData {
        if (element.fileKml != null) {
            // TODO importer le fichier !
        }

        return element.copy(fileKml = null)
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: ImportRisqueKmlData) {
        // no-op
    }
}
