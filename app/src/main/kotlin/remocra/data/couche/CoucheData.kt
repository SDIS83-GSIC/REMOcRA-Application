package remocra.data.couche

import remocra.db.jooq.remocra.enums.SourceCarto
import java.util.UUID

data class CoucheData(
    val coucheId: UUID,
    val coucheCode: String,
    val coucheLibelle: String,
    val coucheSource: SourceCarto,
    val coucheProjection: String?,
    val coucheUrl: String,
    val coucheNom: String?,
    val coucheFormat: String?,
    val coucheCrossOrigin: String?,
    val couchePublic: Boolean,
    val coucheActive: Boolean,
    val coucheProxy: Boolean,
    val groupeFonctionnalitesWithFlagLimiteZc: List<GroupeFonctionnalitesWithFlagLimiteZc>,
    val moduleList: String?,
    val coucheProtected: Boolean,
    val coucheTuilage: Boolean,
    val coucheIconeUrl: String? = null,
    val coucheLegendeUrl: String? = null,
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
