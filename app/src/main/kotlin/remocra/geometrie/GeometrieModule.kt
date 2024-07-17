package remocra.geometrie

import com.fasterxml.jackson.databind.module.SimpleModule

class GeometrieModule : SimpleModule() {
    init {
        addSerializer(GeometrieSerializer())
    }
}
