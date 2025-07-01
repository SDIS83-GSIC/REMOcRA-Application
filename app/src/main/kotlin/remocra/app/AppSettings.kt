package remocra.app

import remocra.data.enums.CodeSdis
import remocra.data.enums.Environment

data class AppSettings(val environment: Environment, val codeSdis: CodeSdis, val epsg: Epsg, val nexsis: Nexsis) {
    // Le fichier est créé dans le Dockerfile, il n'existe pas en dév
    val version = javaClass.getResource("/REMOCRA_VERSION")?.readText()?.trim() ?: "DEV"

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
    val testToken: String?,
)
