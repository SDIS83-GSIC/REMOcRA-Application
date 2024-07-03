package remocra.api

import fr.sdis83.remocra.authn.ApiRole
import fr.sdis83.remocra.authn.ApiUserInfo
import remocra.GlobalConstants
import java.util.UUID
import java.util.stream.Stream

/**
 * Classe regroupant les utilitaires ayant attrait aux cas d'utilisation des PEI
 */
object PeiUtils {
    /**
     * Vérifie si l'organisme connecté est le profil API_ADMIN
     *
     * @param organisme OrganismeIdType
     * @return boolean
     */
    fun isApiAdmin(organisme: OrganismeIdType): Boolean {
        return organisme.isApiAdmin
    }

    fun isServicePublicDECI(organisme: OrganismeIdType): Boolean {
        return GlobalConstants.COMMUNE.equals(organisme.typeOrganisme, true) || GlobalConstants.EPCI.equals(organisme.typeOrganisme, true)
    }

    fun isMaintenanceDECI(organisme: OrganismeIdType): Boolean {
        return Stream.of(
            GlobalConstants.SERVICE_EAUX,
            GlobalConstants.PRESTATAIRE_TECHNIQUE,
            GlobalConstants.COMMUNE,
            GlobalConstants.EPCI,
        )
            .anyMatch { it.equals(organisme.typeOrganisme, true) }
    }

    /**
     * Fonction utilitaire permettant de savoir si l'organisme est la maintenance DECI du PEI
     *
     * @param peiMaintenanceDeciId Id de l'organisme reponsable de la "maintenance DECI"
     * @param organisme OrganismeIdType
     * @return boolean
     */
    fun isMaintenanceDECI(
        peiMaintenanceDeciId: UUID?,
        organisme: OrganismeIdType,
    ): Boolean {
        return peiMaintenanceDeciId != null && peiMaintenanceDeciId == organisme.idOrganisme && isMaintenanceDECI(organisme)
    }

    /**
     * Fonction utilitaire permettant de savoir si l'organisme est le service public DECI du PEI
     *
     * @param peiServicePublicDeciId Id de l'organisme public marqué comme "service public DECI"
     * @param organisme OrganismeIdType
     * @return boolean
     */
    fun isServicePublicDECI(
        peiServicePublicDeciId: UUID?,
        organisme: OrganismeIdType,
    ): Boolean {
        return peiServicePublicDeciId != null && peiServicePublicDeciId == organisme.idOrganisme && (isServicePublicDECI(organisme))
    }

    /**
     * Fonction utilitaire permettant de savoir si l'organisme est le service des eaux du PEI
     *
     * @param peiServiceEauxId Id du service des eaux du PEI
     * @param organisme OrganismeIdType
     * @return boolean
     */
    fun isServiceEaux(peiServiceEauxId: UUID?, organisme: OrganismeIdType): Boolean {
        return peiServiceEauxId != null && peiServiceEauxId == organisme.idOrganisme && GlobalConstants.SERVICE_EAUX.equals(organisme.typeOrganisme, true)
    }

    /**
     * Classe de représentation des propriétés "utiles" d'un organisme utilisateur de l'API
     */
    class OrganismeIdType(
        val idOrganisme: UUID,
        val typeOrganisme: String,
        val isApiAdmin: Boolean,
    ) {
        constructor(userInfo: ApiUserInfo) : this(
            userInfo.userId(),
            userInfo.type(),
            userInfo.roles().stream().anyMatch { it.equals(ApiRole.ADMINISTRER) },
        )
    }
}
