package remocra.data

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.locationtech.jts.geom.Point
import remocra.geometrie.GeometryToGeoJsonSerializer
import java.time.ZonedDateTime
import java.util.UUID

open class ModeleMinimalPeiData(
    open val peiId: UUID,
    open val peiNumeroComplet: String,
    open val natureCode: String,
    open val isDisponible: Boolean,
    @param:JsonSerialize(using = GeometryToGeoJsonSerializer::class)
    open val geometrie: Point,
    open val codeInsee: String,
    open val communeLibelle: String,
    open val idGestion: String?,
    open val nomGest: String?,
    open val peiNumeroInterne: String,
    open val typeRD: String?,
    open val diametre: Int?,
    open val pibiDiametreCanalisation: Int?,
    open val natureLibelle: String?,
    open val natureDeci: String,
    open val site: String?,
    open val adresse: String,
    open val pibiPressionDynamique: Double?,
    open val pibiPression: Double?,
    open val pibiDebit: Int?,
    open val penaVolumeConstate: Int?,
    open val instantChangementDispo: ZonedDateTime?,
    open val dateMiseEnService: ZonedDateTime?,
    open val dateMiseAJour: ZonedDateTime?,
    open val dateDernierControleTechnique: ZonedDateTime?,
    open val dateDerniereRop: ZonedDateTime?,
    open val precision: String?,
    open val isNonConforme: Boolean?,
    open val isAccessibleHbe: Boolean?,
)
