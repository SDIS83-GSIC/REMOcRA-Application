package remocra.web.carto

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.WrappedUserInfo
import remocra.db.CoucheMetadataRepository
import remocra.db.CoucheRepository
import remocra.db.DroitsRepository
import remocra.db.jooq.remocra.enums.SourceCarto
import remocra.db.jooq.remocra.enums.TypeModule
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

class LayersRetriever
@Inject
constructor(
    private val droitsRepository: DroitsRepository,
    private val coucheRepository: CoucheRepository,
    private val coucheMetadataRepository: CoucheMetadataRepository,
) {
    fun getData(module: TypeModule, userInfo: WrappedUserInfo): List<LayerGroupData> {
        val profil = userInfo.utilisateurId?.let {
            droitsRepository.getGroupeFonctionnalitesFromUser(it)
        }

        val coucheMap = coucheRepository.getCoucheMap(module, profil, userInfo.isSuperAdmin)
        val groupeCoucheList = coucheRepository.getGroupeCoucheList()

        return groupeCoucheList.map { group ->
            LayerGroupData(
                id = group.groupeCoucheId,
                code = group.groupeCoucheCode,
                libelle = group.groupeCoucheLibelle,
                ordre = group.groupeCoucheOrdre,
                layers = coucheMap[group.groupeCoucheId].let { it ?: listOf() }.map { couche ->
                    LayerData(
                        id = couche.coucheId,
                        code = couche.coucheCode,
                        ordre = couche.coucheOrdre,
                        source = couche.coucheSource,
                        projection = couche.coucheProjection,
                        libelle = couche.coucheLibelle,
                        url = if (couche.coucheFromGeoserver == true) {
                            UriBuilder.fromPath(AuthnConstants.API_PATH)
                                .path(GeoserverEndpoint::class.java)
                                .path(GeoserverEndpoint::proxy.javaMethod)
                                .build(module, couche.coucheCode)
                                .toString()
                        } else if (couche.coucheProxy == true) {
                            // Si la couche doit utiliser le proxy, on passe par notre CartoEndpoint
                            UriBuilder.fromPath(AuthnConstants.API_PATH)
                                .path(CartoEndpoint::class.java)
                                .path(CartoEndpoint::proxy.javaMethod)
                                .build(module, couche.coucheCode)
                                .toString()
                        } else {
                            // Sinon on utilise directement l'url renseignée
                            couche.coucheUrl
                        },
                        format = couche.coucheFormat,
                        crossOrigin = couche.coucheCrossOrigin,
                        layer = couche.coucheNom,
                        active = couche.coucheActive,
                        icone = couche.coucheIcone?.let {
                            UriBuilder.fromPath(AuthnConstants.API_PATH)
                                .path(LayersEndpoint::class.java)
                                .path(LayersEndpoint::getIcone.javaMethod)
                                .build(couche.coucheId)
                                .toString()
                        },
                        legende = couche.coucheLegende?.let {
                            UriBuilder.fromPath(AuthnConstants.API_PATH)
                                .path(LayersEndpoint::class.java)
                                .path(LayersEndpoint::getLegende.javaMethod)
                                .build(couche.coucheId)
                                .toString()
                        },
                        hasStyle = coucheMetadataRepository.checkCoucheMetadata(couche.coucheId),
                        tuilage = couche.coucheTuilage,
                        opacite = couche.coucheOpacite?.toDouble() ?: 1.0,
                    )
                },
            )
        }
    }

    data class LayerGroupData(
        val id: UUID,
        val code: String,
        val libelle: String,
        val ordre: Int,
        val layers: List<LayerData>,
    )

    data class LayerData(
        val id: UUID,
        val code: String,
        val ordre: Int,
        val source: SourceCarto,
        val projection: String?,
        val libelle: String,
        val layer: String?,
        val url: String,
        val crossOrigin: String?,
        val format: String?,
        val active: Boolean = false,
        val icone: String?,
        val legende: String?,
        val hasStyle: Boolean,
        val tuilage: Boolean,
        val opacite: Double,
    )
}
