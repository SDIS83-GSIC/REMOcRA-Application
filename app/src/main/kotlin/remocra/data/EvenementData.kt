package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.EvenementStatut
import java.time.ZonedDateTime
import java.util.UUID

data class EvenementData(
    val evenementId: UUID,
    val evenementTypeId: UUID,
    val evenementLibelle: String,
    val evenementDescription: String?,
    val evenementOrigine: String?,
    val evenementDateConstat: ZonedDateTime,
    val evenementImportance: Int?,
    val evenementTag: String?,
    val evenementEstFerme: Boolean?,
    var evenementDateCloture: ZonedDateTime?,
    val evenementGeometrie: Geometry?,
    val listeDocument: DocumentsData.DocumentsEvenement?,
    val evenementCriseId: UUID,
    var evenementStatut: EvenementStatut,
    val evenementUtilisateurId: UUID?,
)
