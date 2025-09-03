package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.data.PeiForApiMobileData
import remocra.usecase.AbstractUseCase
import remocra.utils.DisponibiliteDecorator

class DecorateListPeiForApi : AbstractUseCase() {
    @Inject
    lateinit var dispoDecorator: DisponibiliteDecorator

    fun execute(listeHydrant: List<PeiForApiMobileData>): List<PeiForApiMobileData> {
        for (pei in listeHydrant) {
            // Adresse complète
            var adresse = "<div>"
            adresse += if (pei.peiEnFace == true) "Face à " else ""
            adresse += ensureData(pei.peiNumeroVoie, " ")
            adresse += ensureData(pei.peiSuffixeVoie, " ")
            adresse += if (pei.peiVoieId != null) {
                ensureData(pei.peiVoieLibelle, "<br />")
            } else {
                ensureData(pei.peiVoieTexte, "<br />")
            }
            adresse += ensureData(pei.peiComplementAdresse, "<br/>")
            adresse += ensureData(pei.lieuDitLibelle, "<br/>")
            adresse += ensureData(pei.communeCodePostal, " ")
            adresse += ensureData(pei.communeLibelle, "</div>")

            pei.adresseComplete = adresse

            // Disponibilités
            pei.dispoTerrestreString = dispoDecorator.decorateDisponibilite(pei.dispoTerrestre)
            pei.dispoHbeString = pei.dispoHbe?.let { dispoDecorator.decorateDisponibilite(it) }
        }

        return listeHydrant
    }

    private fun ensureData(data: String?, caractereDeFin: String): String {
        if (data != null) {
            return data + caractereDeFin
        }
        return ""
    }
}
