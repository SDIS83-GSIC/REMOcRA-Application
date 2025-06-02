package remocra.apimobile

/**
 * Classe utilitaire permettant de vérifier qu'une version de l'application mobile est bien
 * autorisée à discuter avec une version du serveur. A chaque nouvelle version compatible de l'appli
 * mobile, il conviendra d'ajouter une entrée dans [MobileVersion]
 */
class CompatibleVersions {
    // Versions autorisées pour l'appli mobile
    private val mobileCompatibleVersions: MutableList<String?> =
        mutableListOf<String?>(MobileVersion.M3_0.version)

    /**
     * Permet de savoir si la version de l'application mobile est compatible avec celle du serveur
     *
     * @param mobileVersionString : la version de la tablette
     */
    fun checkCompat(mobileVersionString: String) {
        require(mobileVersionString.isNotEmpty()) { "MobileVersion nulle" }

        require(mobileCompatibleVersions.contains(mobileVersionString)) { "Version non compatible : $mobileVersionString" }
    }

    internal enum class MobileVersion(val version: String) {
        M2_0("2.0"),
        M2_1("2.1"),
        M2_2("2.2"),
        M2_3("2.3"),
        M3_0("3.0"),
    }
}
