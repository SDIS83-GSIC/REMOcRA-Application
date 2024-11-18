package remocra.app

import remocra.data.enums.CodeSdis
import remocra.data.enums.Environment

data class AppSettings(val environment: Environment, val codeSdis: CodeSdis, val epsg: Epsg, val version: String, val nexsis: Nexsis) {
    val srid: Int
        get() = epsg.name.split(":")[1].toInt()
}

data class Epsg(val name: String, val projection: String)

data class Nexsis(
    val mock: Boolean,
    val codeStructure: String?,
    val enabled: Boolean,
    val url: String,
    val user: String?,
    val password: String?,
)
