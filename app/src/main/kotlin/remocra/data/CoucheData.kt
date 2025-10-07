package remocra.data

import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.enums.TypeModule
import java.util.UUID

data class CoucheData(
    val coucheId: UUID = UUID.randomUUID(),
    val coucheCode: String,
    val coucheLibelle: String,
    val coucheOrdre: Int,
    val coucheSource: SourceCarto,
    val coucheProjection: String?,
    val coucheUrl: String,
    val coucheNom: String?,
    val coucheFormat: String?,
    val coucheCrossOrigin: String?,
    val couchePublic: Boolean,
    val coucheActive: Boolean,
    val coucheProxy: Boolean,
    val coucheIconeUrl: String?,
    val coucheLegendeUrl: String?,
    val groupeFonctionnalitesList: Collection<UUID> = listOf(),
    val moduleList: Collection<TypeModule> = listOf(),
    val coucheProtected: Boolean = false,
)

data class SimplifiedCoucheData(
    val coucheId: UUID = UUID.randomUUID(),
    val coucheLibelle: String?,
    val coucheCode: String?,
    val coucheNom: String?,
    val groupeFonctionnaliteList: Collection<GroupeFonctionnalite> = listOf(),
)

data class GroupeFonctionnalite(
    val groupeFonctionnaliteId: UUID,
    val groupeFonctionnaliteCode: String,
    val groupeFonctionnaliteLibelle: String,
)

data class CoucheStyleInput(
    val layerStyleId: UUID? = UUID.randomUUID(),
    val layerId: UUID,
    val layerStyle: String? = null,
    val layerStyleFlag: Boolean = false,
    val layerProfilId: Collection<UUID>? = null,
    val layerStylePublicAccess: Boolean = false,
)

data class GroupeFonctionnaliteList(
    val profilId: UUID,
    val profilLibelle: String? = null,
)

data class ResponseCouche(
    val groupeCoucheId: UUID,
    val groupeCoucheLibelle: String? = null,
    val coucheLibelle: String? = null,
    val coucheMetadataActif: Boolean = false,
    val coucheMetadataPublic: Boolean = false,
    val styleId: UUID,
    val coucheId: UUID? = null,
    val groupeFonctionnaliteList: Collection<GroupeFonctionnaliteList>? = emptyList(),
)

data class CoucheStyle(
    val layerId: UUID,
    val layerStyleFlag: Boolean = false,
    val layerStyle: String? = null,
    val layerStylePublicAccess: Boolean = false,
    val groupLayerId: UUID,
    val layerProfilId: Collection<UUID>? = emptyList(),
)
