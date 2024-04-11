package remocra.app

import com.google.inject.AbstractModule
import com.google.inject.Provides
import jakarta.inject.Singleton
import java.time.Clock
import java.time.ZoneId

class AppModule() : AbstractModule() {
    @Provides @Singleton
    fun provideClock() = Clock.system(ZoneId.systemDefault())

    override fun configure() {
    }
}
