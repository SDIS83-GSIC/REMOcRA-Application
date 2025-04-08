package remocra.data

import java.util.UUID

class GlobalData {

    data class IdCodeLibelleData(val id: UUID, val code: String, val libelle: String)

    data class IdCodeLibelleLienData(
        val id: UUID,
        val code: String,
        val libelle: String,
        val lienId: UUID?,
    )

    data class IdCodeLibellePprifData(
        val id: UUID,
        val code: String,
        val libelle: String,
        val pprif: Boolean,
    )

    data class IdLibelleData(
        val id: UUID,
        val libelle: String,
    )
}
