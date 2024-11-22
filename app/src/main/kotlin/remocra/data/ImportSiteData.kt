package remocra.data

import org.locationtech.jts.geom.Geometry

/**
 * Data class permettant le traitement d'import d'UN site
 */
data class ImportSiteData(val geometrie: Geometry, val code: String, val libelle: String)
