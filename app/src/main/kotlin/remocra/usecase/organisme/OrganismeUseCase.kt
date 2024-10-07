package remocra.usecase.organisme

import com.google.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.tables.pojos.Organisme

class OrganismeUseCase {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    fun getOrganismeForSelect(): List<IdCodeLibelleData> = organismeRepository.getOrganismeForSelect()
    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> = organismeRepository.getAutoriteDeciForSelect()
    fun getServicePublicForSelect(): List<IdCodeLibelleData> = organismeRepository.getServicePublicForSelect()
    fun getActiveOrganisme(): Collection<Organisme> = organismeRepository.getActive()
}
