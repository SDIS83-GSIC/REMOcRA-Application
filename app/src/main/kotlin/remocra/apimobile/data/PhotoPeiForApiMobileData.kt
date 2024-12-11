package remocra.apimobile.data

import java.util.UUID

class PhotoPeiForApiMobileData(
    val photoId: UUID,
    val peiId: UUID,
    val photoDate: String,
    val photoInputStream: ByteArray,
    val photoLibelle: String,
)
