package remocra.apimobile.data

import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import java.util.UUID

data class PeiForApiMobileData(
    val peiId: UUID,
    val natureId: UUID,
    val natureDeciId: UUID,
    val dispoHbe: Disponibilite?,
    val dispoTerrestre: Disponibilite,
    val x: Double,
    val y: Double,
    val lon: Double,
    val lat: Double,
    val peiNumeroComplet: String?,
    val peiTypePei: TypePei,
    val peiEnFace: Boolean?,
    val peiNumeroVoie: String?,
    val peiSuffixeVoie: String?,
    val peiVoieId: UUID?,
    val peiVoieLibelle: String?,
    val peiVoieTexte: String?,
    val peiComplementAdresse: String?,
    val communeCodePostal: String?,
    val communeLibelle: String,
    val lieuDitId: UUID?,
    val lieuDitLibelle: String?,
    val peiObservation: String?,
    val gestionnaireId: UUID?,

    var adresseComplete: String?,

)
