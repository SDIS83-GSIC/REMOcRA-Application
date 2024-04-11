package remocra.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.jooq.JSONB

class JSONBSerializer : StdSerializer<JSONB>(JSONB::class.java) {
    override fun serialize(value: JSONB, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeRawValue(value.data())
    }
}
