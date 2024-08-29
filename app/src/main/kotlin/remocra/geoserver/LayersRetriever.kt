package remocra.geoserver

import jakarta.inject.Inject
import jakarta.ws.rs.core.UriBuilder
import remocra.auth.AuthnConstants
import remocra.auth.UserInfo
import remocra.db.CoucheRepository
import remocra.db.DroitsRepository
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

class LayersRetriever {
    @Inject
    private lateinit var droitsRepository: DroitsRepository

    @Inject
    private lateinit var coucheRepository: CoucheRepository

    fun getData(userInfo: UserInfo?): List<LayerGroupData> {
        val profil = userInfo?.utilisateurId?.let {
            droitsRepository.getProfilDroitListFromUser(it)
        }

        val coucheMap = coucheRepository.getCoucheMap(profil)
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
                        url = couche.coucheUrl,
                        format = couche.coucheFormat,
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
        val source: String,
        val projection: String,
        val libelle: String,
        val layer: String,
        val url: String,
        val format: String,
        val active: Boolean = false,
        val icone: String?,
        val legende: String?,
    )
}
