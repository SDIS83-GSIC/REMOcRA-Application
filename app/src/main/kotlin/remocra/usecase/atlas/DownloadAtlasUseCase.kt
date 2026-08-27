package remocra.usecase.atlas

import jakarta.inject.Inject
import jakarta.ws.rs.core.StreamingOutput
import remocra.auth.WrappedUserInfo
import remocra.db.AtlasRepository
import remocra.usecase.AbstractUseCase
import remocra.usecase.atlas.utils.ZipUseCase

class DownloadAtlasUseCase @Inject constructor(
    private val atlasRepository: AtlasRepository,
    private val atlasTypesSeparatedUseCase: AtlasTypesSeparatedUseCase,
    private val zipService: ZipUseCase,
) : AbstractUseCase() {

    fun execute(userInfo: WrappedUserInfo): StreamingOutput {
        val atlasElements = atlasTypesSeparatedUseCase.getAtlasDocsIds(userInfo)

        return zipService.createZipWithPdf(
            atlasElements.atlasDocument
                ?.let { atlasRepository.getDocumentsByIds(it) }.orEmpty(),
            atlasRepository.getAtlasAnnexes().filter { it.atlasAnnexeIsVisible },
            atlasElements.atlasAnnexe
                ?.let { atlasRepository.getDocumentsByIds(it) }.orEmpty(),
        )
    }
}
