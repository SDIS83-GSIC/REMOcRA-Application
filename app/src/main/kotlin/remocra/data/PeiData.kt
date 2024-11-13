package remocra.data

import org.locationtech.jts.geom.Geometry
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import java.util.UUID

/**
 * Modèle général de représentation d'un PEI pour utilisation dans le back et le front ;
 * ce modèle est décliné (hérité) pour chaque type (PIBI, PENA) afin de rajouter la sémantique nécessaire à ces spécificités.
 */
open class PeiData(
    open val peiId: UUID = UUID.randomUUID(),
    open var peiNumeroComplet: String?,
    open var peiNumeroInterne: Int?,
    open var peiDisponibiliteTerrestre: Disponibilite,
    open val peiTypePei: TypePei,

    open val peiGeometrie: Geometry,

    open val peiAutoriteDeciId: UUID?,
    open val peiServicePublicDeciId: UUID?,
    open val peiMaintenanceDeciId: UUID?,

    open val peiCommuneId: UUID,
    open val peiVoieId: UUID?,
    open val peiNumeroVoie: String?,
    open val peiSuffixeVoie: String?,
    open val peiVoieTexte: String?,
    open val peiLieuDitId: UUID?,
    open val peiCroisementId: UUID?,
    open val peiComplementAdresse: String?,
    open val peiEnFace: Boolean = false,

    open val peiDomaineId: UUID,
    open val peiNatureId: UUID,
    open val peiSiteId: UUID?,
    open val peiGestionnaireId: UUID?,
    open val peiNatureDeciId: UUID,
    open val peiZoneSpecialeId: UUID?,
    open val peiAnneeFabrication: Int?,
    open val peiNiveauId: UUID?,
    open val peiObservation: String?,

    // Attributs supplémentaires pour le recalcul de la numérotation à l'enregistrement ; non modifiables
    open val peiNumeroInterneInitial: Int? = peiNumeroInterne,
    open val peiCommuneIdInitial: UUID? = peiCommuneId,
    open val peiZoneSpecialeIdInitial: UUID? = peiZoneSpecialeId,
    open val peiNatureDeciIdInitial: UUID? = peiNatureDeciId,
    open val peiDomaineIdInitial: UUID? = peiDomaineId,
) {
    val coordonneeX: Double
        get() = peiGeometrie.coordinate.x
    val coordonneeY: Double
        get() = peiGeometrie.coordinate.y
    val srid: Int
        get() = peiGeometrie.srid
}

data class PibiData(
    override val peiId: UUID = UUID.randomUUID(),
    override var peiNumeroComplet: String?,
    override var peiNumeroInterne: Int?,
    override var peiDisponibiliteTerrestre: Disponibilite,
    override val peiTypePei: TypePei,
    override val peiGeometrie: Geometry,

    override val peiAutoriteDeciId: UUID?,
    override val peiServicePublicDeciId: UUID?,
    override val peiMaintenanceDeciId: UUID?,

    override val peiCommuneId: UUID,
    override val peiVoieId: UUID?,
    override val peiNumeroVoie: String?,
    override val peiSuffixeVoie: String?,
    override val peiVoieTexte: String?,
    override val peiLieuDitId: UUID?,
    override val peiCroisementId: UUID?,
    override val peiComplementAdresse: String?,
    override val peiEnFace: Boolean = false,

    override val peiDomaineId: UUID,
    override val peiNatureId: UUID,
    override val peiSiteId: UUID?,
    override val peiGestionnaireId: UUID?,
    override val peiNatureDeciId: UUID,
    override val peiZoneSpecialeId: UUID?,
    override var peiAnneeFabrication: Int?,
    override val peiNiveauId: UUID?,
    override val peiObservation: String?,

    override val peiNumeroInterneInitial: Int? = peiNumeroInterne,
    override val peiCommuneIdInitial: UUID? = peiCommuneId,
    override val peiZoneSpecialeIdInitial: UUID? = peiZoneSpecialeId,
    override val peiNatureDeciIdInitial: UUID? = peiNatureDeciId,
    override val peiDomaineIdInitial: UUID? = peiDomaineId,

    var pibiDiametreId: UUID?,
    val pibiServiceEauId: UUID?,
    val pibiNumeroScp: String?,
    var pibiRenversable: Boolean = false,
    var pibiDispositifInviolabilite: Boolean = false,
    var pibiModeleId: UUID?,
    var pibiMarqueId: UUID?,
    val pibiReservoirId: UUID?,
    val pibiDebitRenforce: Boolean = false,
    var pibiTypeCanalisationId: UUID?,
    var pibiTypeReseauId: UUID?,
    var pibiDiametreCanalisation: Int?,
    var pibiSurpresse: Boolean = false,
    var pibiAdditive: Boolean = false,
    var pibiJumeleId: UUID?,
    // TODO à compléter au fur et à mesure
) : PeiData(
    peiId,
    peiNumeroComplet,
    peiNumeroInterne,
    peiDisponibiliteTerrestre,
    peiTypePei,
    peiGeometrie,
    peiAutoriteDeciId,
    peiServicePublicDeciId,
    peiMaintenanceDeciId,
    peiCommuneId,
    peiVoieId,
    peiNumeroVoie,
    peiSuffixeVoie,
    peiVoieTexte,
    peiLieuDitId,
    peiCroisementId,
    peiComplementAdresse,
    peiEnFace,
    peiDomaineId,
    peiNatureId,
    peiSiteId,
    peiGestionnaireId,
    peiNatureDeciId,
    peiZoneSpecialeId,
    peiAnneeFabrication,
    peiNiveauId,
    peiObservation,
    peiNumeroInterneInitial,
    peiCommuneIdInitial,
    peiZoneSpecialeIdInitial,
    peiNatureDeciIdInitial,
    peiDomaineIdInitial,
)

data class PenaData(
    override val peiId: UUID = UUID.randomUUID(),
    override var peiNumeroComplet: String?,
    override var peiNumeroInterne: Int?,
    override var peiDisponibiliteTerrestre: Disponibilite,
    override val peiTypePei: TypePei,
    override val peiGeometrie: Geometry,

    override val peiAutoriteDeciId: UUID?,
    override val peiServicePublicDeciId: UUID?,
    override val peiMaintenanceDeciId: UUID?,

    override val peiCommuneId: UUID,
    override val peiVoieId: UUID?,
    override val peiNumeroVoie: String?,
    override val peiSuffixeVoie: String?,
    override val peiVoieTexte: String?,
    override val peiLieuDitId: UUID?,
    override val peiCroisementId: UUID?,
    override val peiComplementAdresse: String?,
    override val peiEnFace: Boolean = false,

    override val peiDomaineId: UUID,
    override val peiNatureId: UUID,
    override val peiSiteId: UUID?,
    override val peiGestionnaireId: UUID?,
    override val peiNatureDeciId: UUID,
    override val peiZoneSpecialeId: UUID?,
    override val peiAnneeFabrication: Int?,
    override val peiNiveauId: UUID?,
    override val peiObservation: String?,

    override val peiNumeroInterneInitial: Int? = peiNumeroInterne,
    override val peiCommuneIdInitial: UUID? = peiCommuneId,
    override val peiZoneSpecialeIdInitial: UUID? = peiZoneSpecialeId,
    override val peiNatureDeciIdInitial: UUID? = peiNatureDeciId,
    override val peiDomaineIdInitial: UUID? = peiDomaineId,

    val penaDisponibiliteHbe: Disponibilite,
    var penaCapacite: Int? = null,
    var penaCapaciteIllimitee: Boolean = false,
    var penaCapaciteIncertaine: Boolean = false,
    var penaQuantiteAppoint: Double?,
    var penaMateriauId: UUID? = null,
) : PeiData(
    peiId,
    peiNumeroComplet,
    peiNumeroInterne,
    peiDisponibiliteTerrestre,
    peiTypePei,
    peiGeometrie,
    peiAutoriteDeciId,
    peiServicePublicDeciId,
    peiMaintenanceDeciId,
    peiCommuneId,
    peiVoieId,
    peiNumeroVoie,
    peiSuffixeVoie,
    peiVoieTexte,
    peiLieuDitId,
    peiCroisementId,
    peiComplementAdresse,
    peiEnFace,
    peiDomaineId,
    peiNatureId,
    peiSiteId,
    peiGestionnaireId,
    peiNatureDeciId,
    peiZoneSpecialeId,
    peiAnneeFabrication,
    peiNiveauId,
    peiObservation,
    peiNumeroInterneInitial,
    peiCommuneIdInitial,
    peiZoneSpecialeIdInitial,
    peiNatureDeciIdInitial,
    peiDomaineIdInitial,
)
