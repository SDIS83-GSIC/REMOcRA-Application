package remocra.utils

import jakarta.inject.Inject
import remocra.app.ParametresProvider
import remocra.data.enums.ParametreEnum
import remocra.db.jooq.remocra.enums.Disponibilite

/**
 * Classe de décoration des disponibilités.
 * Pour l'instant seul le libellé "Non conforme" est paramétrable.
 * @param parametresProvider le provider des paramètres applicatifs
 *
 */
class DisponibiliteDecorator {

    @Inject
    lateinit var parametresProvider: ParametresProvider

    fun decorateDisponibilite(disponibilite: Disponibilite): String {
        val libelleNonConforme = parametresProvider.getParametreString(ParametreEnum.PEI_LIBELLE_NON_CONFORME.name)?.takeUnless { it.isBlank() } ?: "Non conforme"
        return when (disponibilite) {
            Disponibilite.DISPONIBLE -> "Disponible"
            Disponibilite.INDISPONIBLE -> "Indisponible"
            Disponibilite.NON_CONFORME -> libelleNonConforme
        }
    }
}
