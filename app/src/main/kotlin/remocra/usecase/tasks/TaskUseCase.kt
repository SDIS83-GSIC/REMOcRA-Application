package remocra.usecase.tasks

import jakarta.inject.Inject
import remocra.db.GestionnaireRepository
import remocra.db.OrganismeRepository
import remocra.db.UtilisateurRepository
import remocra.tasks.Destinataire
import remocra.tasks.TypeDestinataire
import remocra.usecase.AbstractUseCase
import java.util.UUID

class TaskUseCase() : AbstractUseCase() {
    @Inject lateinit var organismeRepository: OrganismeRepository

    @Inject lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject lateinit var utilisateurRepository: UtilisateurRepository

    fun getDestinataireByListPei(listPeiId: List<UUID>, contactRole: String, typeDestinataire: TypeDestinataire): MutableMap<Destinataire, List<UUID?>> {
        val destinataireContactOrganisme: Map<Destinataire, List<UUID?>> =
            if (typeDestinataire.contactOrganisme.isNotEmpty()) {
                organismeRepository.getDestinataireContactOrganisme(
                    listePeiId = listPeiId,
                    typeOrganisme = typeDestinataire.contactOrganisme.toList(),
                    contactRole = contactRole,
                )
            } else { mapOf<Destinataire, List<UUID?>>() }

        val destinataireContactGestionnaire: Map<Destinataire, List<UUID?>> =
            if (typeDestinataire.contactGestionnaire) {
                gestionnaireRepository.getDestinataireContactGestionnaire(
                    listePeiId = listPeiId,
                    contactRole = contactRole,
                )
            } else { mapOf<Destinataire, List<UUID?>>() }

        val destinataireUtilisateurOrganisme: Map<Destinataire, List<UUID?>> =
            if (typeDestinataire.utilisateurOrganisme.isNotEmpty()) {
                utilisateurRepository.getDestinataireUtilisateurOrganisme(
                    listePeiId = listPeiId,
                    typeOrganisme = typeDestinataire.utilisateurOrganisme.toList(),
                )
            } else { mapOf() }

        val destinataireSaisieLibre: MutableMap<Destinataire, List<UUID?>> = mutableMapOf()
        if (typeDestinataire.saisieLibre.isNotEmpty()) {
            typeDestinataire.saisieLibre.forEach { emailLibre ->
                destinataireSaisieLibre[
                    Destinataire(
                        destinataireId = null,
                        destinataireCivilite = null,
                        destinataireFonction = null,
                        destinataireNom = null,
                        destinatairePrenom = null,
                        destinataireEmail = emailLibre,
                    ),
                ] = listPeiId
            }
        }

        val mapPeiIdParDestinataire: MutableMap<Destinataire, List<UUID?>> = mutableMapOf()
        mapPeiIdParDestinataire.putAll(destinataireContactOrganisme)
        mapPeiIdParDestinataire.putAll(destinataireContactGestionnaire)
        mapPeiIdParDestinataire.putAll(destinataireUtilisateurOrganisme)
        mapPeiIdParDestinataire.putAll(destinataireSaisieLibre)

        return mapPeiIdParDestinataire
    }
}
