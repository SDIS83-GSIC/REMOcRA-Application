package remocra.data

import remocra.db.jooq.remocra.enums.TypeModule
import java.util.UUID

data class CoucheData(
    val coucheId: UUID = UUID.randomUUID(),
    val coucheCode: String,
    val coucheLibelle: String,
    val coucheOrdre: Int,
    val coucheSource: String,
    val coucheProjection: String,
    val coucheUrl: String,
    val coucheNom: String,
    val coucheFormat: String,
    val couchePublic: Boolean,
    val coucheActive: Boolean,
    val coucheProxy: Boolean,
    val coucheIconeUrl: String?,
    val coucheLegendeUrl: String?,
    val profilDroitList: Collection<UUID> = listOf(),
    val moduleList: Collection<TypeModule> = listOf(),
)
