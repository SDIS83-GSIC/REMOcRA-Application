package remocra.auth

import remocra.data.AuteurTracabiliteData
import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.tables.pojos.GroupeFonctionnalites
import remocra.db.jooq.remocra.tables.pojos.Utilisateur
import remocra.db.jooq.remocra.tables.pojos.ZoneIntegration
import java.util.UUID

class WrappedUserInfo {

    var userInfo: UserInfo? = null

    var organismeInfo: OrganismeInfo? = null

    /**
     * Méthodes déléguées de [UserInfo]
     */
    val utilisateur: Utilisateur?
        get() = userInfo?.utilisateur

    val droits: Collection<Droit>?
        get() = userInfo?.droits
    val groupeFonctionnalites: GroupeFonctionnalites?
        get() = userInfo?.groupeFonctionnalites

    val zoneCompetence: ZoneIntegration?
        get() = userInfo?.zoneCompetence

    val utilisateurId: UUID?
        get() = userInfo?.utilisateurId

    val prenom: String?
        get() = userInfo?.prenom

    val nom: String?
        get() = userInfo?.nom

    val organismeId: UUID?
        get() {
            if (userInfo != null) {
                return userInfo!!.organismeId
            } else if (organismeInfo != null) {
                return organismeInfo!!.organismeId
            }
            return null
        }

    val affiliatedOrganismeIds: Set<UUID>?
        get() = userInfo?.affiliatedOrganismeIds

    val isActif: Boolean?
        get() = userInfo?.isActif

    val isSuperAdmin: Boolean
        get() {
            if (userInfo != null) {
                return userInfo!!.isSuperAdmin
            } else if (organismeInfo != null) {
                return organismeInfo!!.droits.contains(DroitApi.ADMINISTRER)
            }
            return false
        }

    /**
     * Méthodes déléguées de [OrganismeInfo]

     */
    val droitsApi: Collection<DroitApi>?
        get() = organismeInfo?.droits

    /**
     * Retourne un objet [AuteurTracabiliteData] rempli avec les infos du profil connecté (User ou Organisme)
     */

    fun getInfosTracabilite(): AuteurTracabiliteData {
        // On regarde le type concret de l'utilisateur wrappé
        val user = userInfo
        val organisme = organismeInfo
        if (user != null) {
            return AuteurTracabiliteData(
                idAuteur = user.utilisateurId,
                nom = user.nom,
                prenom = user.prenom,
                email = user.email,
                typeSourceModification = typeSourceModification,
            )
        } else if (organisme != null) {
            return AuteurTracabiliteData(
                idAuteur = organisme.organismeId,
                nom = organisme.libelle,
                prenom = organisme.code,
                email = organisme.email,
                typeSourceModification = typeSourceModification,
            )
        }
        throw IllegalStateException("WrappedUserInfo : userInfo et organismeInfo NULL")
    }

    /**
     * Méthode utilitaire permettant d'aggréger les tests sur le type concret de l'utilisateur wrappé.
     * Certains useCases étant appelés par les 2 contextes, on rend nullable chacun des paramètres pour éviter de les re-spécifier à chaque fois
     */
    fun hasDroits(droitsWeb: Collection<Droit>? = null, droitsApi: Collection<DroitApi>? = null): Boolean {
        // On vérifie les droits dans chaque type concret
        if (!droitsWeb.isNullOrEmpty() && userInfo != null) {
            return userInfo!!.droits.intersect(droitsWeb).isNotEmpty()
        } else if (!droitsApi.isNullOrEmpty() && organismeInfo != null) {
            return organismeInfo!!.droits.intersect(droitsApi).isNotEmpty()
        }
        // A défaut, on retourne un false plutôt qu'un exception pour éviter de faire planter les transactions.
        return false
    }

    /**
     * Méthode utilitaire permettant d'aggréger les tests sur le type concret de l'utilisateur wrappé.
     Facilitateur pour @see [hasDroits] lorsqu'on a un droit unitaire à tester
     */
    fun hasDroit(droitWeb: Droit? = null, droitApi: DroitApi? = null): Boolean {
        return hasDroits(
            droitsWeb = droitWeb.let { if (it != null) setOf(it) else null },
            droitsApi = droitApi.let { if (it != null) setOf(it) else null },
        )
    }

    /**
     * Méthode utilitaire permettant d'aggréger les tests sur le type concret de l'utilisateur wrappé.
     Facilitateur pour @see [hasDroits] lorsqu'on a un droit unitaire à tester
     */
    fun hasDroits(droitWeb: Droit? = null, droitsApi: Collection<DroitApi>? = null): Boolean {
        return hasDroits(
            droitsWeb = droitWeb.let { if (it != null) setOf(it) else null },
            droitsApi = droitsApi,
        )
    }

    /**
     * Permet de récupérer la source de la modification : REMOcRA Web, API ou Mobile
     */
    val typeSourceModification: TypeSourceModification
        get() = (userInfo?.typeSourceModification ?: organismeInfo ?: typeSourceModification) as TypeSourceModification
}
