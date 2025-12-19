package remocra.data.couche

data class CoucheData(
    val coucheId: String,
    val coucheCode: String,
    val coucheLibelle: String,
    val coucheSource: String,
    val coucheProjection: String?,
    val coucheUrl: String,
    val coucheNom: String?,
    val coucheFormat: String?,
    val couchePublic: Boolean,
    val coucheActive: Boolean,
    val coucheProxy: Boolean,
    val groupeFonctionnalitesWithFlagLimiteZc: List<GroupeFonctionnalitesWithFlagLimiteZc>,
    val moduleList: String?,
    val coucheProtected: Boolean,
    val coucheTuilage: Boolean,
) {

    val groupeFonctionnalitesZc: String = groupeFonctionnalitesWithFlagLimiteZc
        .filter { it.limiteZc }
        .map { it.groupeFonctionnaliteId }.joinToString()

    val groupeFonctionnalitesHorsZc: String = groupeFonctionnalitesWithFlagLimiteZc.filter { !it.limiteZc }
        .map { it.groupeFonctionnaliteId }.joinToString()
}

data class GroupeFonctionnalitesWithFlagLimiteZc(
    val groupeFonctionnaliteId: String,
    val limiteZc: Boolean,
)
