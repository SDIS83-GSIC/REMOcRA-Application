package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.eventbus.EventBus
import remocra.eventbus.mobile.MobileExportLogEvent
import remocra.usecase.AbstractUseCase

class ExportLogUseCase @Inject constructor(
    val eventBus: EventBus,
) : AbstractUseCase() {

    fun execute(
        tabletteId: String,
        fichierLogBytes: ByteArray,

    ) {
        eventBus.post(
            MobileExportLogEvent(
                tabletteId = tabletteId,
                fichierLogBytes = fichierLogBytes,
            ),
        )
    }
}
