package remocra.apiapachehop.data

import com.fasterxml.jackson.annotation.JsonProperty

data class NotifierData(
    @param:JsonProperty("liste_email")
    val listeEmail: List<String>,
)
