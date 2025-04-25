package remocra.auth

import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.remocra.enums.DroitApi
import java.util.UUID

class OrganismeInfo(
    val organismeId: UUID,
    val code: String,
    val libelle: String,
    val email: String,
    val droits: Set<DroitApi>,
    val typeOrganismeCode: String,
    val typeSourceModification: TypeSourceModification,
)
