package remocra.web.messagelongueindispo

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object MessagePeiLongueIndispoModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(MessagePeiLongueIndispoEndpoint::class)
    }
}
