package remocra.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.locationtech.jts.geom.Point
import remocra.geometrie.GeometryToGeoJsonSerializer
import java.time.ZonedDateTime
import java.util.UUID

open class ModeleMinimalPeiData(

    @JsonProperty("id")
    open val peiId: UUID,
    @JsonProperty("idSdis")
    open val peiNumeroComplet: String,
    @JsonProperty("typePei")
    open val natureCode: String,
    @JsonProperty("disponible")
    open val isDisponible: Boolean,
    @JsonSerialize(using = GeometryToGeoJsonSerializer::class)
    open val geometrie: Point,
    open val codeInsee: String,
    @JsonProperty("commune")
    open val communeLibelle: String,
    open val idGestion: String?,
    open val nomGest: String?,
    @JsonProperty("refTerr")
    open val peiNumeroInterne: String,
    @JsonProperty("typeRd")
    open val typeRD: String?,
    @JsonProperty("diamPei")
    open val diametre: Int?,
    @JsonProperty("diamCana")
    open val pibiDiametreCanalisation: Int?,
    @JsonProperty("sourcePei")
    open val natureLibelle: String?,
    @JsonProperty("statut")
    open val natureDeci: String,
    @JsonProperty("nomEtab")
    open val site: String?,
    @JsonProperty("situation")
    open val adresse: String,
    @JsonProperty("pressDyn")
    open val pibiPressionDynamique: Double?,
    @JsonProperty("pressStat")
    open val pibiPression: Double?,
    @JsonProperty("debit")
    open val pibiDebit: Int?,
    @JsonProperty("volume")
    open val penaVolumeConstate: Int?,
    @JsonProperty("dateDispo")
    open val instantChangementDispo: ZonedDateTime?,
    @JsonProperty("dateMes")
    open val dateMiseEnService: ZonedDateTime?,
    @JsonProperty("dateMaj")
    open val dateMiseAJour: ZonedDateTime?,
    @JsonProperty("dateCt")
    open val dateDernierControleTechnique: ZonedDateTime?,
    @JsonProperty("dateRo")
    open val dateDerniereRop: ZonedDateTime?,
    @JsonProperty("prec")
    open val precision: String?,
    @JsonProperty("nonConforme")
    open val isNonConforme: Boolean?,
    @JsonProperty("accessibleHbe")
    open val isAccessibleHbe: Boolean?,
)
