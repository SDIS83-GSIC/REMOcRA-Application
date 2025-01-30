package remocra.data

import java.util.UUID

/**
 * Classe Data de base pour les "nomenclature code-libellé-actif(-protected?)"
 * Par extension, on peut manipuler une FK (id-libellé) au travers des 2 champs idoine (nullable)
 */
data class NomenclatureCodeLibelleData(
    val id: UUID,
    val code: String,
    val libelle: String,
    val actif: Boolean,
    val protected: Boolean,

    val idFk: UUID?,
    val libelleFk: String?,

    // propriété nullable permettant de gérer l'accessibilité du bouton "supprimer" de la liste en fonction de l'utilisation de l'élément en tant que FK ; le set est soit empty (pas de dépendance), soit rempli avec le nom des tables (à décorer)
    var tablesDependantes: Set<String>? = null,
)
