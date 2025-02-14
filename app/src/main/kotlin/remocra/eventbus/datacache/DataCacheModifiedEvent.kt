package remocra.eventbus.datacache

import remocra.data.enums.TypeDataCache
import remocra.eventbus.Event

/**
 * Evénement déclenché lors de la modification d'une nomenclature de type typeDataCache.
 * Va permettre de reconstituer le cache du typeDataCache spécifié.
 */
class DataCacheModifiedEvent(val typeDataCache: TypeDataCache) : Event
