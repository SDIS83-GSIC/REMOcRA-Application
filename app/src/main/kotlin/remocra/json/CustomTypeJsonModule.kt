package remocra.json

import com.fasterxml.jackson.databind.module.SimpleModule

class CustomTypeJsonModule : SimpleModule() {
    init {
        addSerializer(JSONBSerializer())
    }
}
