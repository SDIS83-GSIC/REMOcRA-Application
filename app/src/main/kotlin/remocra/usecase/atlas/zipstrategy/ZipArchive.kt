package remocra.usecase.atlas.zipstrategy

import remocra.data.ZipResource

data class ZipArchive(
    val resources: List<ZipResource>,
) {
    fun filesIn(folder: String): List<ZipResource.File> =
        resources
            .filterIsInstance<ZipResource.File>()
            .filter {
                it.path.startsWith("$folder/") ||
                    it.path.contains("/$folder/")
            }
}
