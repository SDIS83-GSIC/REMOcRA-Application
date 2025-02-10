package remocra.utils

import remocra.db.jooq.remocra.tables.pojos.Voie

class AdresseDecorator {

    /**
     * Retourne une adresse décorée en fonction des différents attributs élémentaires fournis.
     */
    fun decorateAdresse(adresse: AdresseForDecorator): String {
        val chunks: MutableList<String?> = mutableListOf()
        if (adresse.enFace == true) {
            chunks.add("Face à")
        }

        chunks.add(adresse.numeroVoie)
        chunks.add(adresse.suffixeVoie)

        chunks.add(
            if (adresse.voie != null) {
                adresse.voie.voieLibelle
            } else {
                adresse.voieTexte
            },
        )
        chunks.add(
            adresse.complementAdresse,
        )

        return chunks.filter { !it.isNullOrEmpty() }.joinToString(separator = " ").trim()
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
