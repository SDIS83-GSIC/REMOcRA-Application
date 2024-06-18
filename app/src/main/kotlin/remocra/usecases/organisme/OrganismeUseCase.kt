package remocra.usecases.organisme

import com.google.inject.Inject
import remocra.data.GlobalData.IdLibelleData
import remocra.db.OrganismeRepository

class OrganismeUseCase {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    fun getOrganismeForSelect(): List<IdLibelleData> = organismeRepository.getOrganismeForSelect()
    fun getAutoriteDeciForSelect(): List<IdLibelleData> = organismeRepository.getAutoriteDeciForSelect()
    fun getServicePublicForSelect(): List<IdLibelleData> = organismeRepository.getServicePublicForSelect()
}
