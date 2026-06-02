package remocra.eventbus.mobile

import remocra.eventbus.Event

data class MobileExportLogEvent(
    val tabletteId: String,
    val fichierLogBytes: ByteArray,
) : Event
