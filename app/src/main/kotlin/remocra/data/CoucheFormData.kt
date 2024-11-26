package remocra.data

import jakarta.servlet.http.Part

data class CoucheFormData(
    val data: List<GroupeCoucheData>,
    val iconeList: List<CoucheImageData>,
    val legendeList: List<CoucheImageData>,
)

data class CoucheImageData(
    val code: String,
    val data: Part,
)
