package remocra.utils

import remocra.db.jooq.remocra.tables.pojos.Voie

class AdresseDecorator {

    /**
     * Retourne une adresse décorée en fonction des différents attributs élémentaires fournis.
     */
    fun decorateAdresse(adresse: AdresseForDecorator): String {
        var adresseString = ""
        if (adresse.enFace == true) {
            adresseString += "Face à "
        }
        adresseString += "${adresse.numeroVoie} ${adresse.suffixeVoie}".trim()
        adresseString += if (adresse.voie != null) {
            " ${adresse.voie.voieLibelle}"
        } else {
            " ${adresse.voieTexte}"
        }
        adresse.complementAdresse?.let { adresseString += " $it" }
        return adresseString.trim()
    }
}

data class AdresseForDecorator(
    val enFace: Boolean?,
    val numeroVoie: String?,
    val suffixeVoie: String?,
    val voie: Voie?,
    val voieTexte: String?,
    val complementAdresse: String?,
)
