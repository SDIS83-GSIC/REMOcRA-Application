package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.EvenementRepository.DocumentEvenementData
import remocra.db.jooq.remocra.enums.EvenementStatut
import java.time.ZonedDateTime
import java.util.UUID

data class EvenementData(
    val evenementId: UUID,
    val evenementCriseId: UUID,
    val evenementTypeId: UUID,
    val evenementLibelle: String?,
    val evenementDescription: String?,
    val evenementOrigine: String?,
    val evenementDateConstat: ZonedDateTime?,
    val evenementImportance: Int?,
    val evenementTag: String?,
    val evenementEstFerme: Boolean?,
    val evenementDateCloture: ZonedDateTime?,
    val evenementGeometrie: Geometry?,
    val listeDocument: DocumentsData.DocumentsEvenement?,
    val evenementStatut: EvenementStatut?,
    val evenementUtilisateurId: UUID?,
    val documents: Collection<DocumentEvenementData>? = null,
)
