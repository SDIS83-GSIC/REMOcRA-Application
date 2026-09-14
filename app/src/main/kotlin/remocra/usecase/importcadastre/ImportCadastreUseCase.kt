package remocra.usecase.importcadastre

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.db.TransactionManager
import remocra.log.LogManagerFactory
import remocra.tasks.ImportCadastreParameters
import remocra.tasks.ImportCadastreTask
import remocra.usecase.AbstractUseCase

class ImportCadastreUseCase @Inject constructor(
    private val task: ImportCadastreTask,
    private val logManagerFactory: LogManagerFactory,
) : AbstractUseCase() {
    fun execute(userInfo: WrappedUserInfo, millesime: String?, supprimerDonneesCadastreNonUtilisees: Boolean, remplacerDonneesCadastre: Boolean, transactionManager: TransactionManager? = null) {
        task.start(
            logManagerFactory.create(),
            userInfo,
            ImportCadastreParameters().apply {
                this.millesime = millesime
                this.supprimerDonneesCadastreNonUtilisees = supprimerDonneesCadastreNonUtilisees
                this.remplacerDonneesCadastre = remplacerDonneesCadastre
            },
            transactionManager,
        )
    }
}
