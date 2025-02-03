package remocra.data.oldeb

import jakarta.servlet.http.Part
import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.TypeCivilite
import java.time.ZonedDateTime
import java.util.UUID

data class OldebFormInput(
    val oldeb: OldebForm,
    val propriete: OldebProprieteForm?, // doit être présent en création
    val locataire: OldebLocataireForm? = null,
    val visiteList: List<OldebVisiteForm>? = listOf(),
    val documentList: List<Part>? = null,
)

data class OldebForm(
    val oldebId: UUID = UUID.randomUUID(),
    val oldebGeometrie: Geometry,
    val oldebCommuneId: UUID,
    val oldebCadastreSectionId: UUID,
    val oldebCadastreParcelleId: UUID,
    val oldebOldebTypeAccesId: UUID?,
    val oldebOldebTypeZoneUrbanismeId: UUID?,
    val oldebNumVoie: String?,
    val oldebVoieId: UUID?,
    val oldebLieuDitId: UUID?,
    val oldebVolume: Int,
    val oldebLargeurAcces: Int?,
    val oldebPortailElectrique: Boolean,
    val oldebCodePortail: String?,
    val oldebActif: Boolean?,
    val caracteristiqueList: List<UUID> = listOf(),
)

data class OldebProprieteForm(
    val oldebProprieteOldebProprietaireId: UUID,
    val oldebProprieteOldebTypeResidenceId: UUID,
)

data class OldebLocataireForm(
    val oldebLocataireId: UUID = UUID.randomUUID(),
    val oldebLocataireOrganisme: Boolean,
    val oldebLocataireRaisonSociale: String?,
    val oldebLocataireCivilite: TypeCivilite,
    val oldebLocataireNom: String,
    val oldebLocatairePrenom: String,
    val oldebLocataireTelephone: String?,
    val oldebLocataireEmail: String?,
)

data class OldebVisiteForm(
    val oldebVisiteId: UUID = UUID.randomUUID(),
    val oldebVisiteCode: String,
    val oldebVisiteDateVisite: ZonedDateTime?,
    val oldebVisiteAgent: String,
    val oldebVisiteObservation: String?,
    val oldebVisiteUtilisateur: UUID?,
    val oldebVisiteDebroussaillementParcelleId: UUID,
    val oldebVisiteDebroussaillementAccesId: UUID,
    val oldebVisiteOldebTypeAvisId: UUID,
    val oldebVisiteOldebTypeActionId: UUID,
    val anomalieList: List<UUID>? = listOf(),
    val suiteList: List<OldebVisiteSuiteForm>? = listOf(),
    val documentList: List<OldebVisiteDocument>? = listOf(),
)

data class OldebVisiteDocument(
    val documentId: UUID,
    val documentNom: String,
    val documentUrl: String,
)

data class OldebVisiteSuiteForm(
    val oldebVisiteSuiteId: UUID = UUID.randomUUID(),
    val oldebVisiteSuiteOldebTypeSuiteId: UUID,
    val oldebVisiteSuiteDate: ZonedDateTime?,
    val oldebVisiteSuiteObservation: String?,
)
