package remocra.eventbus.mobile

import com.google.common.eventbus.Subscribe
import jakarta.inject.Inject
import jakarta.inject.Provider
import remocra.auth.UserInfo
import remocra.auth.WrappedUserInfo
import remocra.data.enums.TypeSourceModification
import remocra.db.UtilisateurRepository
import remocra.eventbus.EventListener
import remocra.log.LogManagerFactory
import remocra.tasks.MobileExportLogParameters
import remocra.tasks.MobileExportLogTask

class MobileExportLogEventListener @Inject constructor(
    private val logManagerFactory: LogManagerFactory,
    private val taskProvider: Provider<MobileExportLogTask>,
    private val utilisateurRepository: UtilisateurRepository,
) : EventListener<MobileExportLogEvent> {
    @Subscribe
    override fun onEvent(event: MobileExportLogEvent) {
        val utilisateurSysteme = utilisateurRepository.getUtilisateurSysteme()
        taskProvider.get().start(
            logManager = logManagerFactory.create(),
            WrappedUserInfo().apply {
                userInfo = UserInfo(
                    utilisateur = utilisateurSysteme,
                    droits = setOf(),
                    zoneCompetence = null,
                    affiliatedOrganismeIds = setOf(),
                    groupeFonctionnalites = null,
                    typeSourceModification = TypeSourceModification.MOBILE,
                )
            },
            MobileExportLogParameters().apply {
                tabletteId = event.tabletteId
                fichierLogBytes = event.fichierLogBytes
            },
        )
    }
}
