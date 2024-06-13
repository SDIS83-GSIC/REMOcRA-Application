package fr.sdis83.remocra.authn

enum class ApiRole(private val label: String) {
    RECEVOIR(RoleType.RECEVOIR),
    TRANSMETTRE(RoleType.TRANSMETTRE),

    ADMINISTRER(RoleType.ADMINISTRER),
    ;

    object RoleType {
        const val RECEVOIR: String = "RECEVOIR"
        const val TRANSMETTRE: String = "TRANSMETTRE"
        const val ADMINISTRER: String = "ADMINISTRER"
    }

    override fun toString(): String {
        return this.label
    }
}
