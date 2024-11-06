package remocra.usecase.module

import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import remocra.auth.UserInfo
import remocra.data.Params
import remocra.db.ModuleRepository
import remocra.db.ProfilDroitRepository
import remocra.db.ThematiqueRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import java.time.ZonedDateTime
import java.util.UUID

class ModuleDocumentCourrierUseCase : AbstractUseCase() {

    @Inject
    lateinit var profilDroitRepository: ProfilDroitRepository

    @Inject
    lateinit var moduleRepository: ModuleRepository

    @Inject
    lateinit var thematiqueRepository: ThematiqueRepository

    fun execute(
        moduleId: UUID?,
        moduleType: String,
        userInfo: UserInfo?,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Collection<DocumentCourrier> {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        // on va chercher le profil droit de l'utilisateur connecté
        val profilDroitId = getProfilDroit(userInfo)

        val listeThematiqueId = moduleRepository.getModuleThematique().map { it.thematiqueId }

        // Puis on va chercher le nombre d'élément à afficher
        val nbDocument = if (moduleId != null) {
            moduleRepository.getById(moduleId).moduleNbDocument ?: 5
        } else {
            null
        }

        // Puis on retourne la liste des documents / courrier
        if (moduleType.uppercase() == TypeModule.DOCUMENT.literal) {
            return thematiqueRepository.getBlocDocumentWithThematique(listeThematiqueId, nbDocument, profilDroitId, params)
                .map {
                    DocumentCourrier(
                        id = it.blocDocumentId,
                        libelle = it.blocDocumentLibelle,
                        date = it.blocDocumentDateMaj,
                    )
                }
        } else {
            // TODO gérer les courriers, pour l'instant on renvoir une liste vide
            return listOf()
        }
    }

    fun count(
        moduleType: String,
        userInfo: UserInfo?,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Int {
        val listeThematiqueId = moduleRepository.getModuleThematique().map { it.thematiqueId }
        if (moduleType.uppercase() == TypeModule.DOCUMENT.literal) {
            return thematiqueRepository.countBlocDocumentWithThematique(listeThematiqueId, getProfilDroit(userInfo), params)
        }

        // TODO courrier
        return 10
    }

    private fun getProfilDroit(userInfo: UserInfo?): UUID {
        if (userInfo == null) {
            throw ForbiddenException()
        }
        // on va chercher le profil droit de l'utilisateur connecté
        return profilDroitRepository.getProfilUtilisateurByUtilisateurId(userInfo.utilisateurId)
    }

    data class DocumentCourrier(
        val id: UUID,
        val libelle: String?,
        val date: ZonedDateTime?,
    )
}
