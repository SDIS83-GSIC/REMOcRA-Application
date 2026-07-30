package remocra.usecase.organisme

import jakarta.inject.Inject
import remocra.data.GlobalData.IdCodeLibelleData
import remocra.db.OrganismeRepository
import remocra.db.jooq.remocra.tables.pojos.Organisme
import remocra.usecase.AbstractUseCase
import java.util.UUID

class OrganismeUseCase
@Inject
constructor(
    private val organismeRepository: OrganismeRepository,
) :
    AbstractUseCase() {

    fun getOrganismeForSelect(affiliatedOrganismeIds: Set<UUID>?): List<IdCodeLibelleData> = organismeRepository.getOrganismeForSelect(affiliatedOrganismeIds)
    fun getOrganismeFilterWithPeiForSelect(listePei: Set<UUID>, affiliatedOrganismeIds: Set<UUID>?): List<IdCodeLibelleData> = organismeRepository.getOrganismeFilterWithPeiForSelect(listePei, affiliatedOrganismeIds)
    fun getAutoriteDeciForSelect(): List<IdCodeLibelleData> = organismeRepository.getAutoriteDeciForSelect()
    fun getServicePublicForSelect(): List<IdCodeLibelleData> = organismeRepository.getServicePublicForSelect()
    fun getActiveOrganisme(): Collection<Organisme> = organismeRepository.getActive()
    fun getOrganismeParentFromType(typeOrganismeId: UUID): Collection<Organisme?> = organismeRepository.getOrganismeParentFromType(typeOrganismeId)
}
