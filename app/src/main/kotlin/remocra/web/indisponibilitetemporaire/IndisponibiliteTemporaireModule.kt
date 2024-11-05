package remocra.web.indisponibilitetemporaire

import com.google.inject.Binder
import com.google.inject.Module
import remocra.web.indisponibiliteTemporaire.IndisponibiliteTemporaireEndPoint
import remocra.web.registerResources

object IndisponibiliteTemporaireModule : Module {
    override fun configure(binder: Binder) {
        binder.registerResources(IndisponibiliteTemporaireEndPoint::class)
    }
}
