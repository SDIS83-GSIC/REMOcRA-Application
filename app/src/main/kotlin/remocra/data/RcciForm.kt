package remocra.data

import jakarta.servlet.http.Part
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.Direction
import java.time.ZonedDateTime
import java.util.UUID

data class RcciFormInput(
    val rcci: RcciForm,
    val documentList: List<Part>? = null,
)

data class RcciForm(
    val rcciId: UUID = UUID.randomUUID(),
    val rcciCommentaireConclusion: String?,
    val rcciComplement: String?,
    val rcciCarroyageDfci: String?,
    val rcciDateIncendie: ZonedDateTime,
    val rcciDateModification: ZonedDateTime,
    val rcciDirectionVent: Direction?,
    val rcciForceVent: Int?,
    val rcciForcesOrdre: String?,
    val rcciGdh: ZonedDateTime?,
    val rcciGelLieux: Boolean?,
    val rcciGeometrie: Geometry,
    val rcciHygrometrie: Int?,
    val rcciIndiceRothermel: Int?,
    val rcciPointEclosion: String,
    val rcciPremierCos: String?,
    val rcciPremierEngin: String?,
    val rcciSuperficieFinale: Double?,
    val rcciSuperficieReferent: Double?,
    val rcciSuperficieSecours: Double?,
    val rcciTemperature: Double?,
    val rcciVentLocal: Boolean?,
    val rcciVoie: String?,
    val rcciCommuneId: UUID?,
    val rcciRcciTypePrometheeCategorieId: UUID?,
    val rcciRcciTypeDegreCertitudeId: UUID?,
    val rcciRcciTypeOrigineAlerteId: UUID,
    val rcciRcciArriveeDdtmOnfId: UUID?,
    val rcciRcciArriveeSdisId: UUID?,
    val rcciRcciArriveeGendarmerieId: UUID?,
    val rcciRcciArriveePoliceId: UUID?,
    val rcciUtilisateurId: UUID,
    val documentList: List<RcciDocument>? = listOf(),
)

data class RcciDocument(
    val documentId: UUID,
    val documentNom: String,
    val documentUrl: String,
)

data class RcciGeometryForm(
    val rcciId: UUID,
    val rcciGeometrie: Geometry,
)
