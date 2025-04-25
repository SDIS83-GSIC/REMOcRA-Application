package remocra.api.usecase

import remocra.api.PeiUtils
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ErrorType
import remocra.db.PeiRepository
import remocra.db.jooq.remocra.tables.pojos.Pei
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractUseCase
import java.util.UUID

/**
 * Regroupe toutes les méthodes nécessaires à la manipulation d'un PEI dans l'API
 */
abstract class AbstractApiPeiUseCase(
    protected open val peiRepository: PeiRepository,
) : AbstractUseCase() {
    /**
     * Vérifie que l'utilisateur a bien les droits sur le PEI, sinon déclenche une [RemocraResponseException]
     */
    @Throws(RemocraResponseException::class)
    fun checkDroits(pei: Pei?, userInfo: WrappedUserInfo) {
        if (pei == null) {
            throw RemocraResponseException(
                ErrorType.PEI_INEXISTANT,
            )
        }

        if (!this.isPeiAccessible(pei.peiId, userInfo)) {
            throw RemocraResponseException(
                ErrorType.FORBIDDEN,
            )
        }
    }

    /**
     * Retourne un PEI spécifique, en passant son numéro complet.
     * Déclenche l'exception idoine si le PEI n'existe pas ou s'il n'est pas accessible
     *
     * @param numeroComplet: String
     * @return [Pei]
     */
    fun getPeiSpecifique(numeroComplet: String, userInfo: WrappedUserInfo): Pei {
        val peiId = peiRepository.getPeiIdFromNumero(numeroComplet)
            ?: throw RemocraResponseException(
                ErrorType.PEI_INEXISTANT,
            )

        if (!this.isPeiAccessible(peiId, userInfo)) {
            throw RemocraResponseException(
                ErrorType.FORBIDDEN,
            )
        }
        return peiRepository.getPeiFromNumero(numeroComplet)!!
    }

    /**
     * Détermine si le PEI spécifié est accessible à l'utilisateur courant
     *
     * @param idPei Le numéro du PEI
     */
    fun isPeiAccessible(idPei: UUID, userInfo: WrappedUserInfo): Boolean {
        return getPeiAccessibilite(idPei, userInfo).isAccessible
    }

    /**
     * Retourne les infos d'accessibilité d'un PEI dont l'ID est passé en paramètre
     *
     * @param idPei String
     * @return PeiAccessibilite
     */
    protected fun getPeiAccessibilite(idPei: UUID, wrapperUserInfo: WrappedUserInfo): PeiAccessibilite {
        return listPeiAccessibilite(setOf(idPei), wrapperUserInfo)[0]
    }

    /**
     * Retourne les propriétés permettant de calculer l'accessibilité des PEI
     *
     *
     *  * si ListPei est vide, de tous les PEI
     *  * sinon, ceux dont l'ID est compris dans la liste
     *
     *
     * @param listPei List<UUID> (Pei.id)
     * @return List<PeiAccessibilite>
     */
    fun listPeiAccessibilite(listPei: Set<UUID>, wrapperUserInfo: WrappedUserInfo): List<PeiAccessibilite> {
        val organisme = PeiUtils.OrganismeIdType(wrapperUserInfo)

        return peiRepository.getPeiAccessibility(listPei).map { PeiAccessibilite(it.id, it.numeroComplet, it.maintenanceDeciId, it.servicePublicDeciId, it.serviceEauxId, PeiUtils.isApiAdmin(organisme) || PeiUtils.isMaintenanceDECI(it.maintenanceDeciId, organisme) || PeiUtils.isServicePublicDECI(it.servicePublicDeciId, organisme) || PeiUtils.isServiceEaux(it.serviceEauxId, organisme)) }
    }
}
