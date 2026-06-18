package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeDebroussaillement
import remocra.db.jooq.remocra.enums.TypeProgramme
import remocra.db.jooq.remocra.enums.TypeTravaux
import remocra.db.jooq.remocra.tables.pojos.DfciDeb
import java.util.UUID

data class DfciDebData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciDebLibelle: String,
    val dfciDebAnneeProgramme: Int?,
    val dfciDebAnneeTravaux: Int?,
    val dfciDebMoisTravaux: Int?,
    val dfciDebAnneeEdition: Int?,
    val dfciDebLargeur: Double,
    val dfciDebSurface: Double,
    val dfciDebGeometrie: Geometry,
    val dfciDebType: TypeDebroussaillement,
    val dfciDebProgramme: TypeProgramme?,
    val dfciDebTravaux: TypeTravaux?,
    val dfciDebRemarque: String?,
    val dfciDebDfciMassifId: UUID,
    val dfciDebDfciOuvrageId: UUID?,
    val dfciDebDfciPrestataireId: UUID?,
) : IdCodeVersion {

    /**
     * Convertie la classe DfciDebData pour récupérer le pojo
     */
    fun convertDebDataToPojo(): DfciDeb =
        DfciDeb(
            dfciDebId = id,
            dfciDebLibelle = dfciDebLibelle,
            dfciDebAnneeProgramme = dfciDebAnneeProgramme,
            dfciDebAnneeTravaux = dfciDebAnneeTravaux,
            dfciDebMoisTravaux = dfciDebMoisTravaux,
            dfciDebAnneeEdition = dfciDebAnneeEdition,
            dfciDebLargeur = dfciDebLargeur,
            dfciDebSurface = dfciDebSurface,
            dfciDebGeometrie = dfciDebGeometrie,
            dfciDebType = dfciDebType,
            dfciDebProgramme = dfciDebProgramme,
            dfciDebTravaux = dfciDebTravaux,
            dfciDebRemarque = dfciDebRemarque,
            dfciDebDfciMassifId = dfciDebDfciMassifId,
            dfciDebDfciOuvrageId = dfciDebDfciOuvrageId,
            dfciDebDfciPrestataireId = dfciDebDfciPrestataireId,
            dfciDebCode = code,
            dfciDebVersion = version,
        )
}
