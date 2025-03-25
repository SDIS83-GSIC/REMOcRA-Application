package remocra.utils

import remocra.db.jooq.remocra.tables.pojos.Voie

class AdresseDecorator {
    companion object {
        // Pour réutilisation dans les requêtes où le Decorator n'est pas directement utilisable
        const val FACE_A = "Face à"
    }

    /**
     * Retourne une adresse décorée en fonction des différents attributs élémentaires fournis.
     * Certaines requêtes sont trop complexes pour utiliser le decorator. A chaque fois que c'est le cas, on met un "@see" à l'endroit en question pour signifier qu'on a redécoupé, et
     * tout refactoring sur cette classe devra aussi prendre en compte la version éclatée.
     */
    fun decorateAdresse(adresse: AdresseForDecorator): String {
        val chunks: MutableList<String?> = mutableListOf()
        if (adresse.enFace == true) {
            chunks.add(FACE_A)
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
