package remocra.couverturehydraulique.graphe

import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import java.util.UUID

/**
 * Structure de graphe optimisée pour la couverture hydraulique.
 * Chaque sommet référence ses voisins via un mapping UUID → Voie pour un accès rapide.
 */
data class Sommet(
    val id: UUID,
    val geometrie: Point,
    val voisins: MutableMap<UUID, Voie> = mutableMapOf(), // UUID du voisin → Voie
)

data class Voie(
    val id: UUID,
    var geometrie: LineString,
    var source: UUID?,
    var destination: UUID?,
    val traversable: Boolean,
    val sensUnique: Boolean,
    val niveau: Int,
    var peiTroncon: UUID? = null,
)

data class VoieLateraleGraphe(
    val id: UUID,
    var voieVoisine: UUID,
    var angle: Double? = null,
    var gauche: Boolean? = null,
    var droite: Boolean? = null,
    var traversable: Boolean? = null,
    var accessible: Boolean? = null,
)

enum class AngleOrdre { ASC, DESC }

const val BUFFER_SIDE_BOTH = "both"
const val BUFFER_SIDE_LEFT = "left"
const val BUFFER_SIDE_RIGHT = "right"
const val BUFFER_ENDCAP_FLAT = "flat"
const val BUFFER_ENDCAP_ROUND = "round"
const val MULTIPOLYGON_TYPE = "MultiPolygon"

/**
 * Graphe en mémoire pour la couverture hydraulique.
 * Instancié et passé explicitement entre les use cases, sans état global.
 */
class Graphe(
    val sommets: MutableMap<UUID, Sommet>,
    val voies: MutableMap<UUID, Voie>,
    val voiesLaterales: MutableList<VoieLateraleGraphe> = mutableListOf(),
)
