package remocra.data

import java.util.UUID

/**
 * Data class qui permet de retourner les différents destinataires en fonction de leur type.
 * La fonction correspond soit à la fonction dans la table contact, soit au profil utilisateur ou au profil organisme
 */
data class DestinataireData(
    val destinataireId: UUID,
    val typeDestinataire: String,
    val nomDestinataire: String,
    val emailDestinataire: String,
    val fonctionDestinataire: String?,
)

enum class TypeDestinataire(val libelle: String) {
    ORGANISME("Organisme"),
    UTILISATEUR("Utilisateur"),
    CONTACT_ORGANISME("Contact d'organisme"),
    CONTACT_GESTIONNAIRE("Contact de gestionnaire"),
}
