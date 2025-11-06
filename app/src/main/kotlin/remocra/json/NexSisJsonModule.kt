package remocra.json

import com.fasterxml.jackson.databind.module.SimpleModule
import remocra.geometrie.GeometryToGeoJsonSerializer

/**
 * Module Jackson pour la sérialisation des objets pour fourniture à NexSIS (uniquement)
 */
class NexSisJsonModule : SimpleModule() {
    init {
        addSerializer(GeometryToGeoJsonSerializer())
    }
}
