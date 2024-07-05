package remocra.data

import remocra.db.jooq.remocra.tables.pojos.Commune
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import java.util.UUID

/**
 * Classe représentant un PEI très light contenant uniquement les attributs nécessaires au calcul de la *numérotation*.
 * Toutes les propriétés sont nullable, car non applicables à tous les SDIS, et on doit prendre en compte le cas de la création du PEI (donc pas encore d'ID)
 * Lorsqu'applicable, on conserve le nom des propriétés des POJO par clarté.
 * Dans le cas d'un id + pojo de la même nomenclature, on charge le POJO à la première occasion pour toute utilisation ultérieure (d'où le *var*)
 */
data class PeiForNumerotationData(
    var peiNumeroInterne: Int?,
    val peiId: UUID?,
    val peiCommuneId: UUID?,
    val peiZoneSpecialeId: UUID?,
    val peiNatureDeciId: UUID?,
    val peiDomaineId: UUID?,

    val nature: Nature? = null,
    var zoneSpeciale: ZoneIntegration? = null,
    var commune: Commune? = null,
    var domaine: Domaine? = null,

)
