package remocra.usecase.carto

import jakarta.inject.Inject
import remocra.data.enums.ErrorType
import remocra.db.CoucheRepository
import remocra.exception.RemocraResponseException
import remocra.geoserver.GeoserverApi
import remocra.geoserver.response.LayerGroupResponse
import remocra.usecase.AbstractUseCase
import java.util.UUID

class LayerGroupUrlUseCase : AbstractUseCase() {

    @Inject lateinit var coucheRepository: CoucheRepository

    @Inject lateinit var geoServerApi: GeoserverApi

    fun execute(coucheId: UUID): LayerGroupResponse {
        val name = coucheRepository.getCoucheById(coucheId).coucheNom?.split(":")?.lastOrNull() ?: throw RemocraResponseException(ErrorType.ADMIN_COUCHES_NAME_NULL)
        val response = geoServerApi.getLayerGroup(name).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("GeoServer erreur : ${response.code()}")
        }
        return response.body() ?: throw RuntimeException("Réponse GeoServer vide")
    }
}
