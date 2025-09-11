package remocra.usecase.module

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DocumentCourrierData
import remocra.data.Params
import remocra.db.GroupeFonctionnalitesRepository
import remocra.db.ModuleRepository
import remocra.db.ThematiqueRepository
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.usecase.AbstractUseCase
import java.util.UUID

class ModuleDocumentCourrierUseCase : AbstractUseCase() {

    @Inject
    lateinit var groupeFonctionnalitesRepository: GroupeFonctionnalitesRepository

    @Inject
    lateinit var moduleRepository: ModuleRepository

    @Inject
    lateinit var thematiqueRepository: ThematiqueRepository

    fun execute(
        moduleId: UUID,
        moduleType: String,
        userInfo: WrappedUserInfo,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Collection<DocumentCourrierData> {
        // on va chercher le groupe de fonctionnalites de l'utilisateur connecté
        val groupeFonctionnalitesId = getGroupeFonctionnalites(userInfo)

        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        // Puis on va chercher le nombre d'élément à afficher
        val nbDocument = if (params != null) {
            moduleRepository.getById(moduleId).moduleNbDocument ?: 5
        } else {
            null
        }

        // Puis on retourne la liste des documents / courrier
        if (moduleType.uppercase() == TypeModule.DOCUMENT.literal) {
            return thematiqueRepository.getDocumentHabilitableWithThematique(listeThematiqueId, nbDocument, groupeFonctionnalitesId, userInfo.isSuperAdmin, params)
                .map {
                    DocumentCourrierData(
                        id = it.documentHabilitableId,
                        libelle = it.documentHabilitableLibelle,
                        date = it.documentHabilitableDateMaj,
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
        userInfo: WrappedUserInfo,
        params: Params<ThematiqueRepository.Filter, ThematiqueRepository.Sort>?,
    ): Int {
        val listeThematiqueId = moduleRepository.getModuleThematiqueByModuleId(moduleId).map { it.thematiqueId }

        return thematiqueRepository.countDocumentHabilitableWithThematique(listeThematiqueId, getGroupeFonctionnalites(userInfo), userInfo.isSuperAdmin, params)
    }

    private fun getGroupeFonctionnalites(userInfo: WrappedUserInfo): UUID? {
        // on va chercher le groupe de fonctionnalites de l'utilisateur connecté
        return groupeFonctionnalitesRepository.getProfilUtilisateurByUtilisateurId(userInfo.utilisateurId!!)
    }
}
