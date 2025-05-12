package remocra.auth

import remocra.db.jooq.remocra.enums.DroitApi
import java.util.UUID

class OrganismeInfo(
    val organismeId: UUID,
    val libelle: String,
    val droits: Set<DroitApi>,
    val typeOrganismeCode: String,
)
