package remocra.data.couverturehydraulique

import org.locationtech.jts.geom.Geometry

data class Reseau(
    val reseauGeometrie: Geometry,
    val reseauTraversable: Boolean,
    val reseauSensUnique: Boolean,
    val reseauNiveau: Int?,
)
