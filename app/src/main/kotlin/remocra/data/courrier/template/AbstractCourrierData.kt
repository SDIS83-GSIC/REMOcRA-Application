package remocra.data.courrier.template

import org.jooq.JSONB
import remocra.authn.UserInfo

/**
 * Correspond aux données des templates
 * Classe regroupant les attributs commun des courriers à afficher dans les courriers
 * @property courrierPath : nom du fichier JRXML dans les ressources qui contient les caractéristiques
 * du PDF
 * @property courrierSubReport : JSON stocké en base contenant les sous-rapports.
 * @property userConnecte : le nom et prénom de l'utilisateur qui génère le PDF
 * @property dateGeneration : la date de génération du PDF
 * @property destinataire : le destinataire (contact) du courrier. On ira chercher le contact correspondant en se basant sur un rôle en base.
 * @property expediteur : l'expéditeur du courrier, le signataire, dépend des SDIS (peut être l'utilisateur connecté, un chef de centre, ...)
 */
abstract class AbstractCourrierData {
    abstract val courrierPath: String
    abstract val courrierSubReport: JSONB?
    abstract val userConnecte: UserInfo
    abstract val dateGeneration: String
    abstract val destinataire: String?
    abstract val destinataireEmail: String?
    abstract val destinataireVoie: String?
    abstract val destinataireVille: String?
    abstract val destinataireCodePostal: String?
    abstract val destinataireLieuDit: String?
    abstract val expediteur: String
}
