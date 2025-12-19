package remocra.data

import jakarta.servlet.http.Part
import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.enums.TypeModule
import java.util.UUID

data class CoucheFormDataWithImage(
    val coucheFormData: CoucheFormData,
    val icone: Part?,
    val legende: Part?,
)

data class CoucheFormData(
    val coucheId: UUID = UUID.randomUUID(),
    val groupeCoucheId: UUID,
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
    val coucheIconeUrl: String?,
    val coucheLegendeUrl: String?,
    val groupeFonctionnalitesZcList: Collection<UUID> = listOf(),
    val groupeFonctionnalitesHorsZcList: Collection<UUID> = listOf(),
    val moduleList: Collection<TypeModule> = listOf(),
    val coucheProtected: Boolean = false,
    val coucheTuilage: Boolean,
)
