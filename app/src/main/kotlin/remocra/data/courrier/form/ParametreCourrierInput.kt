package remocra.data.courrier.form

import jakarta.ws.rs.FormParam
import java.util.UUID

class ParametreCourrierInput(
    @FormParam("modeleCourrierId")
    val modeleCourrierId: UUID,

    @FormParam("listParametres")
    val listParametres: List<NomValue>?,

)

data class NomValue(
    val nom: String,
    val valeur: String?,
)
