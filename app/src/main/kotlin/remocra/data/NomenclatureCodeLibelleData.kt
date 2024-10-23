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
)
