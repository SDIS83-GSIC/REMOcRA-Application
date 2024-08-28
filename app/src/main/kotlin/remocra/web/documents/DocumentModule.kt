package remocra.web.documents

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.registerResources

object DocumentModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(DocumentPeiEndPoint::class, DocumentEndPoint::class)
    }
}
