package remocra.usecase.module

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
import remocra.data.DocumentCourrierData
import remocra.data.Params
import remocra.db.ModuleRepository
import remocra.db.ProfilDroitRepository
import remocra.db.ThematiqueRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import java.util.UUID

class ModuleDocumentCourrierUseCase : AbstractUseCase() {

    @Inject
    lateinit var profilDroitRepository: ProfilDroitRepository

    @Inject
    lateinit var moduleRepository: ModuleRepository

    @Inject
    lateinit var thematiqueRepository: ThematiqueRepository

    fun execute(
        moduleId: UUID,
        moduleType: String,
        userInfo: UserInfo?,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Collection<DocumentCourrierData> {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        // on va chercher le profil droit de l'utilisateur connecté
        val profilDroitId = getProfilDroit(userInfo)

        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        // Puis on va chercher le nombre d'élément à afficher
        val nbDocument = if (params != null) {
            moduleRepository.getById(moduleId).moduleNbDocument ?: 5
        } else {
            null
        }

        // Puis on retourne la liste des documents / courrier
        if (moduleType.uppercase() == TypeModule.DOCUMENT.literal) {
            return thematiqueRepository.getBlocDocumentWithThematique(listeThematiqueId, nbDocument, profilDroitId, params)
                .map {
                    DocumentCourrierData(
                        id = it.blocDocumentId,
                        libelle = it.blocDocumentLibelle,
                        date = it.blocDocumentDateMaj,
                    )
                }
            // COURRIER
        } else {
            return thematiqueRepository.getCourrierWithThematiqueForAccueil(listeThematiqueId, nbDocument, userInfo)
                .map {
                    DocumentCourrierData(
                        id = it.id,
                        libelle = it.libelle,
                        date = it.date,
                    )
                }
        }
    }

    fun count(
        moduleId: UUID,
        moduleType: String,
        userInfo: UserInfo?,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Int {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        return thematiqueRepository.countBlocDocumentWithThematique(listeThematiqueId, getProfilDroit(userInfo), params)
    }

    private fun getProfilDroit(userInfo: UserInfo?): UUID {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        // on va chercher le profil droit de l'utilisateur connecté
        return profilDroitRepository.getProfilUtilisateurByUtilisateurId(userInfo.utilisateurId)
    }
}
