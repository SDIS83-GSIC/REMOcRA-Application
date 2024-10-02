package remocra.usecase.organisme

import com.google.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.OrganismeRepository

class OrganismeUseCase {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    fun getOrganismeForSelect(): List<IdCodeLibelleData> = organismeRepository.getOrganismeForSelect()
    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> = organismeRepository.getAutoriteDeciForSelect()
    fun getServicePublicForSelect(): List<IdCodeLibelleData> = organismeRepository.getServicePublicForSelect()
}
