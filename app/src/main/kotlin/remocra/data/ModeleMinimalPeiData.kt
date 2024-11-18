package remocra.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime
import java.util.UUID

data class ModeleMinimalPeiData(
    val codeStructure: String,

    @JsonProperty("id")
    val peiId: UUID,
    @JsonProperty("idSdis")
    val peiNumeroComplet: String,
    @JsonProperty("typePei")
    val natureCode: String,
    @JsonProperty("disponible")
    val isDisponible: Boolean,
    @JsonProperty("geometrie")
    val geometrie: String,
    val codeInsee: String,
    @JsonProperty("commune")
    val communeLibelle: String,
    val idGestion: String?,
    val nomGest: String?,
    @JsonProperty("refTerr")
    val peiNumeroInterne: String,
    @JsonProperty("typeRd")
    val typeRD: String?,
    @JsonProperty("diamPei")
    val diametre: Int?,
    @JsonProperty("diamCana")
    val pibiDiametreCanalisation: Int?,
    @JsonProperty("sourcePei")
    val natureLibelle: String?,
    @JsonProperty("statut")
    val natureDeci: String,
    @JsonProperty("nomEtab")
    val site: String?,
    @JsonProperty("situation")
    val adresse: String,
    @JsonProperty("pressDyn")
    val pibiPressionDynamique: Double?,
    @JsonProperty("pressStat")
    val pibiPression: Double?,
    @JsonProperty("debit")
    val pibiDebit: Int?,
    @JsonProperty("volume")
    val penaVolumeConstate: Int?,
    @JsonProperty("dateDispo")
    val instantChangementDispo: ZonedDateTime?,
    @JsonProperty("dateMes")
    val dateMiseEnService: String?,
    @JsonProperty("dateMaj")
    val dateMiseAJour: String?,
    @JsonProperty("dateCt")
    val dateDernierControleTechnique: String?,
    @JsonProperty("dateRo")
    val dateDerniereRecop: String?,
    @JsonProperty("prec")
    val precision: String?,
    @JsonProperty("nonConforme")
    val isNonConforme: Boolean?,
    @JsonProperty("accessibleHbe")
    val isAccessibleHbe: Boolean?,
)
