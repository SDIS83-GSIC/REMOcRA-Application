package remocra.web.appsettings

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object AppSettingsModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(AppSettingsEndPoint::class)
    }
}
