package remocra.apimobile.usecase

import remocra.apimobile.data.PeiForApiMobileData
import remocra.usecase.AbstractUseCase

class BuildAdresseCompleteUseCase : AbstractUseCase() {
    fun execute(listeHydrant: List<PeiForApiMobileData>): List<PeiForApiMobileData> {
        for (pei in listeHydrant) {
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
