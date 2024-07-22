package remocra.app

import remocra.data.enums.CodeSdis
import remocra.data.enums.Environment

data class AppSettings(val environment: Environment, val codeSdis: CodeSdis, val sridInt: Int, val sridString: String)
