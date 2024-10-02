package remocra.data

import java.util.UUID

data class EtudeData(
    val etudeId: UUID,
    val typeEtudeId: UUID,
    val etudeNumero: String,
    val etudeLibelle: String,
    val etudeDescription: String?,
    val listeCommuneId: Collection<UUID>?,
    val listeDocument: DocumentsData.DocumentsEtude?,
)
