package remocra.api.data

import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.TypePei
import java.time.ZonedDateTime
import java.util.UUID

data class PeiProprieteCommunePenaPibi(
    val peiId: UUID,
    val peiTypePei: TypePei,
    val natureLibelle: String,
    val peiNatureId: UUID,
    val autoriteDeciLibelle: String?,
    val servicePublicDeciLibelle: String?,
    val peiServicePublicDeciId: UUID?,
    val maintenanceDeciLibelle: String?,
    val peiMaintenanceDeciId: UUID?,
    val natureDeciLibelle: String,
    val communeLibelle: String,
    val peiNumeroVoie: Int?,
    val peiSuffixeVoie: String?,
    val peiEnFace: Boolean,
    val voieLibelle: String?,
    val peiVoieTexte: String?,
    val voieCroisementLibelle: String?,
    val niveauLibelle: String?,
    val domaineLibelle: String,
    val lieuDitLibelle: String?,
    val peiComplementAdresse: String?,
    val lastRop: ZonedDateTime?,
    val lastCTP: ZonedDateTime?,
    val lastNp: ZonedDateTime?,
    val lastReception: ZonedDateTime?,
    val lastRecoInit: ZonedDateTime?,
    val dateDerniereModification: ZonedDateTime?,
    val peiDisponibiliteTerrestre: Disponibilite,
    val hasIndispoTemp: Boolean,
) {
    val dateDerniereVisite = listOfNotNull(lastRop, lastCTP, lastNp, lastReception, lastRecoInit).maxOrNull()
}
