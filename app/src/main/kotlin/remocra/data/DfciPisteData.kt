package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeCroisement
import remocra.db.jooq.remocra.enums.TypeFoncier
import remocra.db.jooq.remocra.enums.TypeImpasse
import remocra.db.jooq.remocra.enums.TypeImpraticabilite
import remocra.db.jooq.remocra.enums.TypeProgramme
import remocra.db.jooq.remocra.enums.TypeTravaux
import remocra.db.jooq.remocra.enums.TypeVoie
import remocra.db.jooq.remocra.tables.pojos.DfciPiste
import java.time.ZonedDateTime
import java.util.UUID

data class DfciPisteData(
    override val id: UUID,
    override val code: String,
    override val version: Int,
    val dfciPisteAdresse: String?,
    val dfciPisteAnneeProgramme: Int?,
    val dfciPisteAnneeTravaux: Int?,
    val dfciPisteCirculation: Boolean,
    val dfciPisteDateGps: ZonedDateTime,
    val dfciPisteLibelle: String,
    val dfciPisteNumero: String,
    val dfciPisteOuverture: Boolean,
    val dfciPisteEstDfci: Boolean,
    val dfciPisteRetournement: Boolean,
    val dfciPisteNumTroncon: Int,
    val dfciPisteNumObjectif: Int?,
    val dfciPisteLibelleObjectif: String?,
    val dfciPisteGeometrie: Geometry,
    val dfciPisteImpraticabilite: TypeImpraticabilite?,
    val dfciPisteTravaux: TypeTravaux?,
    val dfciPisteVoie: TypeVoie,
    val dfciPisteImpasse: TypeImpasse,
    val dfciPisteFoncier: TypeFoncier?,
    val dfciPisteCroisement: TypeCroisement,
    val dfciPisteProgramme: TypeProgramme?,
    val dfciPistePraticabilite: Boolean,
    val dfciPisteRemarque: String?,
    val dfciPisteDfciCategoriePisteId: UUID,
    val dfciPisteDfciMassifId: UUID,
    val dfciPisteDfciPrestataireId: UUID?,
    val dfciPisteDfciOuvrageId: UUID?,
) : IdCodeVersion {
    /**
     * Convertie la classe DfciPisteData pour obtenir un pojo
     */
    fun convertPisteDataToPojo(): DfciPiste =
        DfciPiste(
            dfciPisteId = id,
            dfciPisteAdresse = dfciPisteAdresse,
            dfciPisteAnneeProgramme = dfciPisteAnneeProgramme,
            dfciPisteAnneeTravaux = dfciPisteAnneeTravaux,
            dfciPisteCirculation = dfciPisteCirculation,
            dfciPisteDateGps = dfciPisteDateGps,
            dfciPisteLibelle = dfciPisteLibelle,
            dfciPisteNumero = dfciPisteNumero,
            dfciPisteOuverture = dfciPisteOuverture,
            dfciPisteEstDfci = dfciPisteEstDfci,
            dfciPisteRetournement = dfciPisteRetournement,
            dfciPisteNumTroncon = dfciPisteNumTroncon,
            dfciPisteNumObjectif = dfciPisteNumObjectif,
            dfciPisteLibelleObjectif = dfciPisteLibelleObjectif,
            dfciPisteGeometrie = dfciPisteGeometrie,
            dfciPisteImpraticabilite = dfciPisteImpraticabilite,
            dfciPisteTravaux = dfciPisteTravaux,
            dfciPisteVoie = dfciPisteVoie,
            dfciPisteImpasse = dfciPisteImpasse,
            dfciPisteFoncier = dfciPisteFoncier,
            dfciPisteCroisement = dfciPisteCroisement,
            dfciPisteProgramme = dfciPisteProgramme,
            dfciPistePraticabilite = dfciPistePraticabilite,
            dfciPisteRemarque = dfciPisteRemarque,
            dfciPisteDfciCategoriePisteId = dfciPisteDfciCategoriePisteId,
            dfciPisteDfciMassifId = dfciPisteDfciMassifId,
            dfciPisteDfciPrestataireId = dfciPisteDfciPrestataireId,
            dfciPisteDfciOuvrageId = dfciPisteDfciOuvrageId,
            dfciPisteCode = code,
            dfciPisteVersion = version,
        )
}
