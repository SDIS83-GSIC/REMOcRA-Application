package remocra.web.lieudit

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object LieuDitModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(LieuDitEndPoint::class)
    }
}
