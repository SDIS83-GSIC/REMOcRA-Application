package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL
import org.locationtech.jts.geom.Geometry
import remocra.auth.WrappedUserInfo
import remocra.data.GlobalData
import remocra.db.jooq.entrepotsig.tables.references.V_COMMUNE_SIG
import remocra.db.jooq.remocra.tables.pojos.Commune
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.ZONE_INTEGRATION
import remocra.utils.ST_DWithin
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import java.util.UUID

class CommuneRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getMapById(): Map<UUID, Commune> = dsl.selectFrom(COMMUNE).fetchInto<Commune>().associateBy { it.communeId }

    fun getAll(codeInsee: String?, libelle: String?, limit: Int?, offset: Int?): Collection<Commune> =
        dsl.selectFrom(COMMUNE)
            .where(getConditions(codeInsee, libelle))
            .orderBy(COMMUNE.CODE_INSEE)
            .limit(limit)
            .offset(offset)
            .fetchInto()

    private fun getConditions(codeInsee: String?, libelle: String?): Condition {
        var condition: Condition = DSL.trueCondition()
        if (codeInsee != null) {
            condition = condition.and(COMMUNE.CODE_INSEE.eq(codeInsee))
        }
        if (libelle != null) {
            condition = condition.and(COMMUNE.LIBELLE.likeIgnoreCase("%$libelle%"))
        }
        return condition
    }

    fun getCommuneForSelect(): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.CODE_INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"))
            .from(COMMUNE)
            .orderBy(DSL.field("SUBSTRING(${COMMUNE.LIBELLE}, '([0-9]+)')::integer", Int::class.java), COMMUNE.LIBELLE)
            .fetchInto()

    fun getCommuneForSelectWithZone(zoneId: UUID): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.CODE_INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"))
            .from(COMMUNE)
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(zoneId))
            .where(ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .orderBy(DSL.field("SUBSTRING(${COMMUNE.LIBELLE}, '([0-9]+)')::integer", Int::class.java), COMMUNE.LIBELLE)
            .fetchInto()

    /**
     * Retourne les communes qui sont à moins de PEI_TOLERANCE_COMMUNE_METRES mètres de la géométrie passée en paramètre
     */
    fun getCommunesPei(geometry: Field<Geometry?>, toleranceCommuneMetres: Int): List<GlobalData.IdCodeLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.CODE_INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"))
            .from(COMMUNE)
            .where(
                ST_DWithin(
                    ST_Transform(geometry, SRID),
                    COMMUNE.GEOMETRIE,
                    toleranceCommuneMetres.toDouble(),
                ),
            )
            .orderBy(COMMUNE.LIBELLE)
            .fetchInto()

    fun getCommunePei(geometry: Field<Geometry?>): UUID? =
        dsl.select(COMMUNE.ID)
            .from(COMMUNE)
            .where(
                ST_Within(
                    ST_Transform(geometry, SRID),
                    COMMUNE.GEOMETRIE,
                ),
            )
            .orderBy(COMMUNE.LIBELLE)
            .limit(1)
            .fetchOneInto()

    fun getCommuneByCoords(geometry: Field<Geometry?>): GlobalData.IdCodeLibellePprifData? =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.CODE_INSEE.`as`("code"), COMMUNE.LIBELLE.`as`("libelle"), COMMUNE.PPRIF.`as`("pprif"))
            .from(COMMUNE)
            .where(
                ST_Within(
                    ST_Transform(geometry, SRID),
                    COMMUNE.GEOMETRIE,
                ),
            )
            .orderBy(COMMUNE.LIBELLE)
            .limit(1)
            .fetchOneInto()

    fun getById(id: UUID): Commune = dsl.selectFrom(COMMUNE).where(COMMUNE.ID.eq(id)).fetchSingleInto()

    fun updateFromEntrepotSig(listeChampsAUpdate: List<String>) =
        dsl.update(COMMUNE)
            .set(COMMUNE.LIBELLE, if (listeChampsAUpdate.contains("libelle")) { V_COMMUNE_SIG.LIBELLE } else { COMMUNE.LIBELLE })
            .set(COMMUNE.CODE_POSTAL, if (listeChampsAUpdate.contains("code_postal")) { V_COMMUNE_SIG.CODE_POSTAL } else { COMMUNE.CODE_POSTAL })
            .set(COMMUNE.GEOMETRIE, if (listeChampsAUpdate.contains("geometrie")) { V_COMMUNE_SIG.GEOMETRIE } else { COMMUNE.GEOMETRIE })
            .set(COMMUNE.PPRIF, if (listeChampsAUpdate.contains("pprif")) { V_COMMUNE_SIG.PPRIF } else { COMMUNE.PPRIF })
            .from(V_COMMUNE_SIG)
            .where(COMMUNE.CODE_INSEE.eq(V_COMMUNE_SIG.CODE_INSEE))
            .execute()

    fun getAllCodeInsee(): List<String> = dsl.select(COMMUNE.CODE_INSEE).from(COMMUNE).fetchInto()

    fun insertFromEntrepotSig(listCodeInseeDejaPresent: List<String>) =
        dsl.insertInto(COMMUNE)
            .select(
                DSL.select(
                    V_COMMUNE_SIG.ID,
                    V_COMMUNE_SIG.LIBELLE,
                    V_COMMUNE_SIG.CODE_INSEE,
                    V_COMMUNE_SIG.CODE_POSTAL,
                    V_COMMUNE_SIG.GEOMETRIE,
                    V_COMMUNE_SIG.PPRIF,
                )
                    .from(V_COMMUNE_SIG)
                    .where(V_COMMUNE_SIG.CODE_INSEE.notIn(listCodeInseeDejaPresent)),
            )
            .execute()

    fun getCommuneByZoneIntegrationShortData(userInfo: WrappedUserInfo): Collection<CommuneShortData> {
        if (userInfo.isSuperAdmin) {
            return dsl.select(COMMUNE.ID, COMMUNE.LIBELLE)
                .from(COMMUNE).fetchInto()
        }
        return dsl.select(COMMUNE.ID, COMMUNE.LIBELLE)
            .from(COMMUNE)
            .join(ZONE_INTEGRATION)
            .on(ZONE_INTEGRATION.ID.eq(userInfo.zoneCompetence?.zoneIntegrationId))
            .where(ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE))
            .groupBy(COMMUNE.ID, COMMUNE.LIBELLE)
            .orderBy(COMMUNE.LIBELLE)
            .fetchInto()
    }

    data class CommuneShortData(
        val communeId: UUID,
        val communeLibelle: String,
    )

    fun getCommuneIdLibelleByMotif(userInfo: WrappedUserInfo, motifLibelle: String): Collection<GlobalData.IdLibelleData> =
        dsl.select(COMMUNE.ID.`as`("id"), COMMUNE.LIBELLE.`as`("libelle")).from(
            userInfo.isSuperAdmin.let {
                if (it) {
                    COMMUNE
                } else COMMUNE.join(ZONE_INTEGRATION)
                    .on(ZONE_INTEGRATION.ID.eq(userInfo.zoneCompetence?.zoneIntegrationId))
            },
        )
            .where(
                userInfo.isSuperAdmin.let {
                    if (it) {
                        DSL.noCondition()
                    } else {
                        ST_Within(COMMUNE.GEOMETRIE, ZONE_INTEGRATION.GEOMETRIE)
                    }
                },
            )
            .and(COMMUNE.LIBELLE.containsIgnoreCaseUnaccent(motifLibelle)).orderBy(COMMUNE.LIBELLE).fetchInto()

    fun getGeometrieCommune(communeId: UUID): CommuneGeometryOnly =
        dsl.select(COMMUNE.GEOMETRIE)
            .from(COMMUNE)
            .where(COMMUNE.ID.eq(communeId))
            .fetchSingleInto()

    data class CommuneGeometryOnly(
        val communeGeometry: Geometry,
    )
}
