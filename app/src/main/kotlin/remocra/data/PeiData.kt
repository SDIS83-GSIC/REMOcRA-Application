package remocra.data

import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import java.util.UUID

/**
 * Modèle général de représentation d'un PEI pour utilisation dans le back et le front ;
 * ce modèle est décliné (hérité) pour chaque type (PIBI, PENA) afin de rajouter la sémantique nécessaire à ces spécificités.
 */
open class PeiData(
    open val peiId: UUID,
    open var peiNumeroComplet: String?,
    open var peiNumeroInterne: Int?,
    open var peiDisponibiliteTerrestre: Disponibilite,
    open val peiTypePei: TypePei,

    open val peiAutoriteDeciId: UUID?,
    open val peiServicePublicDeciId: UUID?,
    open val peiMaintenanceDeciId: UUID?,

    open val peiCommuneId: UUID,
    open val peiVoieId: UUID?,
    open val peiNumeroVoie: Int?,
    open val peiSuffixeVoie: String?,
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

    // TODO ajouter la géométrie

    // Attributs supplémentaires pour le recalcul de la numérotation à l'enregistrement ; non modifiables
    val peiNumeroInterneInitial: Int? = peiNumeroInterne,
    val peiCommuneIdInitial: UUID = peiCommuneId,
    val peiZoneSpecialeIdInitial: UUID? = peiZoneSpecialeId,
    val peiNatureDeciIdInitial: UUID = peiNatureDeciId,
    val peiDomaineIdInitial: UUID = peiDomaineId,
)

data class PibiData(
    override val peiId: UUID,
    override var peiNumeroComplet: String?,
    override var peiNumeroInterne: Int?,
    override var peiDisponibiliteTerrestre: Disponibilite,
    override val peiTypePei: TypePei,

    override val peiAutoriteDeciId: UUID?,
    override val peiServicePublicDeciId: UUID?,
    override val peiMaintenanceDeciId: UUID?,

    override val peiCommuneId: UUID,
    override val peiVoieId: UUID?,
    override val peiNumeroVoie: Int?,
    override val peiSuffixeVoie: String?,
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

    val pibiDiametreId: UUID?,
    val pibiServiceEauId: UUID?,
    val pibiNumeroScp: String?,
    val pibiRenversable: Boolean = false,
    val pibiDispositifInviolabilite: Boolean = false,
    val pibiModeleId: UUID?,
    val pibiMarqueId: UUID?,
    val pibiReservoirId: UUID?,
    val pibiDebitRenforce: Boolean = false,
    val pibiTypeCanalisationId: UUID?,
    val pibiTypeReseauId: UUID?,
    val pibiDiametreCanalisation: Int?,
    val pibiSurpresse: Boolean = false,
    val pibiAdditive: Boolean = false,
    // TODO à compléter au fur et à mesure
) : PeiData(
    peiId,
    peiNumeroComplet,
    peiNumeroInterne,
    peiDisponibiliteTerrestre,
    peiTypePei,
    peiAutoriteDeciId,
    peiServicePublicDeciId,
    peiMaintenanceDeciId,
    peiCommuneId,
    peiVoieId,
    peiNumeroVoie,
    peiSuffixeVoie,
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
)

class PenaData(
    override val peiId: UUID,
    override var peiNumeroComplet: String?,
    override var peiNumeroInterne: Int?,
    override var peiDisponibiliteTerrestre: Disponibilite,
    override val peiTypePei: TypePei,

    override val peiAutoriteDeciId: UUID?,
    override val peiServicePublicDeciId: UUID?,
    override val peiMaintenanceDeciId: UUID?,

    override val peiCommuneId: UUID,
    override val peiVoieId: UUID?,
    override val peiNumeroVoie: Int?,
    override val peiSuffixeVoie: String?,
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

    val penaDisponibiliteHbe: Disponibilite? = null,
    val penaCapacite: Int? = null,
    val penaCapaciteIllimitee: Boolean = false,
    val penaCapaciteIncertaine: Boolean = false,
    val penaQuantiteAppoint: Double?,
    val penaMateriauId: UUID? = null,
) : PeiData(
    peiId,
    peiNumeroComplet,
    peiNumeroInterne,
    peiDisponibiliteTerrestre,
    peiTypePei,
    peiAutoriteDeciId,
    peiServicePublicDeciId,
    peiMaintenanceDeciId,
    peiCommuneId,
    peiVoieId,
    peiNumeroVoie,
    peiSuffixeVoie,
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
)
