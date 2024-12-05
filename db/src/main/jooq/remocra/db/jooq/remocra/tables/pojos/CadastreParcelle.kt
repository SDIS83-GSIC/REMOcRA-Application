/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.pojos

import org.locationtech.jts.geom.Geometry
import java.io.Serializable
import java.util.UUID
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
data class CadastreParcelle(
    val cadastreParcelleId: UUID,
    val cadastreParcelleGeometrie: Geometry,
    val cadastreParcelleNumero: String,
    val cadastreParcelleCadastreSectionId: UUID,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (this::class != other::class) {
            return false
        }
        val o: CadastreParcelle = other as CadastreParcelle
        if (this.cadastreParcelleId != o.cadastreParcelleId) {
            return false
        }
        if (this.cadastreParcelleGeometrie != o.cadastreParcelleGeometrie) {
            return false
        }
        if (this.cadastreParcelleNumero != o.cadastreParcelleNumero) {
            return false
        }
        if (this.cadastreParcelleCadastreSectionId != o.cadastreParcelleCadastreSectionId) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + this.cadastreParcelleId.hashCode()
        result = prime * result + this.cadastreParcelleGeometrie.hashCode()
        result = prime * result + this.cadastreParcelleNumero.hashCode()
        result = prime * result + this.cadastreParcelleCadastreSectionId.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("CadastreParcelle (")

        sb.append(cadastreParcelleId)
        sb.append(", ").append(cadastreParcelleGeometrie)
        sb.append(", ").append(cadastreParcelleNumero)
        sb.append(", ").append(cadastreParcelleCadastreSectionId)

        sb.append(")")
        return sb.toString()
    }
}
