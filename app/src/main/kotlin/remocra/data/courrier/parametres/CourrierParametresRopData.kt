package remocra.data.courrier.parametres

import java.util.UUID

class CourrierParametresRopData(
    val communeId: UUID?,
    val gestionnaireId: UUID?,
    val isOnlyPublic: Boolean?,
    val isEPCI: Boolean?, // Peut être envoyé à un maire ou un président d'EPCI
    val profilUtilisateurId: UUID?,
    val annee: String?,
    val expediteurGrade: String?,
    val expediteurStatut: String?,
    val reference: String?,
    val cis: UUID?,

) : AbstractCourrierParametresData()
