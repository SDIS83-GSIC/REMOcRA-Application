package remocra.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import jakarta.inject.Singleton
import remocra.geometrie.GeometrieModule
import remocra.web.registerResource

object JsonModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResource<JacksonJsonProvider>()
    }

    /**
     * Fournit un [ObjectMapper] configuré pour l'application.
     * En cas de modification, penser à mettre à jour aussi [remocra.eventbus.pei.PeiModifiedEventListener.nexSisObjectMapper].
     */
    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(GuavaModule())
            .registerModule(CustomTypeJsonModule())
            .registerModule(GeometrieModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
}
