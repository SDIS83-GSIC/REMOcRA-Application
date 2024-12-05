package remocra.csv

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton
import remocra.json.JacksonJsonProvider
import remocra.web.registerResource

object CsvModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResource<JacksonJsonProvider>()
    }

    @Provides
    @Singleton
    fun provideCsvMapper(): CsvMapper =
        CsvMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .addModule(JavaTimeModule())
            .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build()
}
