package remocra.data.courrier.form

import jakarta.ws.rs.FormParam
import java.util.UUID

class ParametreCourrierInput(
    @param:FormParam("modeleCourrierId")
    val modeleCourrierId: UUID,

    @param:FormParam("courrierReference")
    val courrierReference: String,

    @param:FormParam("listParametres")
    val listParametres: List<NomValue>?,

)

data class NomValue(
    val nom: String,
    val valeur: String?,
)
