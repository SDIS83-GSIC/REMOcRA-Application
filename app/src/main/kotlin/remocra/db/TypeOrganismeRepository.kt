package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.enums.DroitApi
import remocra.db.jooq.remocra.tables.pojos.TypeOrganisme
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import java.util.UUID

class TypeOrganismeRepository @Inject constructor(private val dsl: DSLContext) : NomenclatureRepository<TypeOrganisme>, AbstractRepository() {

    override fun getMapById(): Map<UUID, TypeOrganisme> = dsl.selectFrom(TYPE_ORGANISME).where(TYPE_ORGANISME.ACTIF.isTrue).orderBy(TYPE_ORGANISME.LIBELLE).fetchInto<TypeOrganisme>().associateBy { it.typeOrganismeId }

    fun getAll(limit: Int?, offset: Int?): Collection<TypeOrganisme> =
        dsl.selectFrom(TYPE_ORGANISME)
            .where(TYPE_ORGANISME.ACTIF.isTrue)
            .orderBy(TYPE_ORGANISME.CODE)
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun getAll(): Collection<TypeOrganisme> = getAll(null, null)

    fun getTypeOrganismeWithDroitApi(): Collection<TypeOrganisme> =
        dsl.selectFrom(TYPE_ORGANISME)
            .fetchInto()

    fun updateTypeOrganismeDroitApi(typeOrganismeId: UUID, listeDroitApi: Array<DroitApi?>?) =
        dsl.update(TYPE_ORGANISME)
            .set(TYPE_ORGANISME.DROIT_API, listeDroitApi)
            .where(TYPE_ORGANISME.ID.eq(typeOrganismeId))
            .execute()

    fun getUserTypeOrganisme(organismeId: UUID): String =
        dsl.select(TYPE_ORGANISME.CODE)
            .from(TYPE_ORGANISME)
            .join(ORGANISME)
            .on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ID.eq(organismeId))
            .fetchSingleInto()

    fun getByOrganismeId(organismeId: UUID): String =
        dsl.select(TYPE_ORGANISME.CODE).from(TYPE_ORGANISME)
            .join(ORGANISME)
            .on(ORGANISME.TYPE_ORGANISME_ID.eq(TYPE_ORGANISME.ID))
            .where(ORGANISME.ID.eq(organismeId))
            .fetchSingleInto()
}
