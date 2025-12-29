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
    val coucheTuilage: Boolean,
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

data class ResponseCouche(
    val groupeCoucheId: UUID,
    val groupeCoucheLibelle: String,

    val coucheId: UUID,
    val coucheLibelle: String,

    val coucheMetadataId: UUID,
    val coucheMetadataActif: Boolean = false,
    val coucheMetadataPublic: Boolean = false,

    val groupeFonctionnaliteList: Collection<GroupeFonctionnalite>? = emptyList(),
)

data class CoucheMetadata(
    val groupeCoucheId: UUID,

    val coucheId: UUID,

    val coucheMetadataId: UUID? = UUID.randomUUID(),
    val coucheMetadataActif: Boolean = false,
    val coucheMetadataPublic: Boolean = false,
    val coucheMetadataStyle: String? = null,

    val groupeFonctionnaliteIds: Collection<UUID>? = emptyList(),
)

data class CoucheMetadataWithLibelle(
    val groupeCoucheId: UUID,

    val coucheId: UUID,

    val coucheLibelle: String,

    val coucheMetadataId: UUID? = UUID.randomUUID(),
    val coucheMetadataActif: Boolean = false,
    val coucheMetadataPublic: Boolean = false,
    val coucheMetadataStyle: String? = null,

    val groupeFonctionnaliteIds: Collection<UUID>? = emptyList(),
)
