package remocra.usecase.organisme

import jakarta.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.usecase.AbstractUseCase
import java.util.UUID

class OrganismeUseCase : AbstractUseCase() {
    @Inject
    lateinit var organismeRepository: OrganismeRepository

    fun getOrganismeForSelect(): List<IdCodeLibelleData> = organismeRepository.getOrganismeForSelect()
    fun getOrganismeFilterWithPeiForSelect(listePei: Set<UUID>): List<IdCodeLibelleData> = organismeRepository.getOrganismeFilterWithPeiForSelect(listePei)
    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> = organismeRepository.getAutoriteDeciForSelect()
    fun getServicePublicForSelect(): List<IdCodeLibelleData> = organismeRepository.getServicePublicForSelect()
    fun getActiveOrganisme(): Collection<Organisme> = organismeRepository.getActive()
}
