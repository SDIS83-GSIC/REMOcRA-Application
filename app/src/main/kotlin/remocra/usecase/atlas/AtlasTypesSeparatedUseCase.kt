package remocra.usecase.atlas

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.AtlasElementsIds
import remocra.data.AtlasElementsTemplate
import remocra.db.AtlasRepository
import remocra.usecase.AbstractUseCase

class AtlasTypesSeparatedUseCase @Inject constructor(
    private val atlasRepository: AtlasRepository,
) : AbstractUseCase() {

    fun execute(): AtlasElementsTemplate =
        AtlasElementsTemplate(
            atlasAnnexe = atlasRepository.findAtlasAnnexeFileNames(),
            atlasDocument = atlasRepository.findAtlasDocumentFileNames(),
        )

    fun getAtlasDocsIds(
        userInfo: WrappedUserInfo,
    ): AtlasElementsIds =
        AtlasElementsIds(
            atlasAnnexe = atlasRepository.getAtlasDocsAnnexesIds(),
            atlasDocument = atlasRepository.getAtlasDocumentsIds(userInfo),
        )
}
