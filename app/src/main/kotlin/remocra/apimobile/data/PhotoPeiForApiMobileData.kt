package remocra.apimobile.data

import java.io.InputStream
import java.util.UUID

class PhotoPeiForApiMobileData(
    val photoId: UUID,
    val peiId: UUID,
    val photoDate: String,
    val photoInputStream: InputStream,
    val photoLibelle: String,
)
