package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.CoucheData
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import remocra.db.jooq.remocra.tables.references.L_COUCHE_DROIT
import remocra.db.jooq.remocra.tables.references.L_COUCHE_MODULE
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import java.util.UUID

class CoucheRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    // XXX: Retourner un type plus spécifique, avec uniquement les champs utiles
    fun getCouche(code: String, module: TypeModule, profilDroit: ProfilDroit?, isSuperAdmin: Boolean? = false): Couche? =
        dsl.select(*COUCHE.fields())
            .from(COUCHE)
            .join(L_COUCHE_MODULE).on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID))
            .where(COUCHE.CODE.eq(code))
            .and(L_COUCHE_MODULE.MODULE_TYPE.eq(module))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COUCHE.PUBLIC.isTrue.or(
                        profilDroit?.let {
                            DSL.exists(
                                DSL.select(L_COUCHE_DROIT.COUCHE_ID)
                                    .from(L_COUCHE_DROIT)
                                    .where(L_COUCHE_DROIT.COUCHE_ID.eq(COUCHE.ID))
                                    .and(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(it.profilDroitId)),
                            )
                        },
                    ),
                    isSuperAdmin == true,
                ),
            )
            .fetchOneInto<Couche>()

    /**
     * Retourne une Map<coucheId, CoucheData> pour stockage dans le dataCache
     */
    fun getMapById(): Map<UUID, CoucheData> =
        dsl.selectFrom(COUCHE).fetchInto<Couche>().map {
                couche ->
            CoucheData(
                coucheId = couche.coucheId,
                coucheCode = couche.coucheCode,
                coucheLibelle = couche.coucheLibelle,
                coucheOrdre = couche.coucheOrdre,
                coucheSource = couche.coucheSource,
                coucheProjection = couche.coucheProjection,
                coucheUrl = couche.coucheUrl,
                coucheNom = couche.coucheNom,
                coucheFormat = couche.coucheFormat,
                couchePublic = couche.couchePublic,
                coucheActive = couche.coucheActive,
                coucheProxy = couche.coucheProxy ?: true,
                coucheCrossOrigin = couche.coucheCrossOrigin,
                coucheIconeUrl = null,
                coucheLegendeUrl = null,
                profilDroitList = getProfilDroitList(couche.coucheId).map { profilDroit -> profilDroit.profilDroitId },
                moduleList = getModuleList(couche.coucheId),
                coucheProtected = couche.coucheProtected ?: false,
            )
        }.associateBy { it.coucheId }

    fun getCoucheMap(module: TypeModule, profilDroit: ProfilDroit?, isSuperAdmin: Boolean): Map<UUID, List<Couche>> =
        dsl.selectDistinct(*COUCHE.fields())
            .from(COUCHE)
            .leftJoin(L_COUCHE_DROIT).on(L_COUCHE_DROIT.COUCHE_ID.eq(COUCHE.ID))
            .join(L_COUCHE_MODULE).on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID).and(L_COUCHE_MODULE.MODULE_TYPE.eq(module)))
            .where(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COUCHE.PUBLIC.isTrue.or(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(profilDroit?.profilDroitId)),
                    isSuperAdmin,
                ),
            )
            .orderBy(COUCHE.ORDRE.desc())
            .fetchInto<Couche>().groupBy { it.coucheGroupeCoucheId }

    fun getProfilDroitList(coucheId: UUID): List<ProfilDroit> =
        dsl.select(*PROFIL_DROIT.fields())
            .from(PROFIL_DROIT)
            .join(L_COUCHE_DROIT).on(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .where(L_COUCHE_DROIT.COUCHE_ID.eq(coucheId))
            .fetchInto<ProfilDroit>()

    fun getModuleList(coucheId: UUID): List<TypeModule> =
        dsl.select(L_COUCHE_MODULE.MODULE_TYPE)
            .from(L_COUCHE_MODULE)
            .where(L_COUCHE_MODULE.COUCHE_ID.eq(coucheId))
            .fetchInto<TypeModule>()

    fun getCoucheList(groupeCoucheId: UUID): List<Couche> = dsl.selectFrom(COUCHE).where(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId)).fetchInto<Couche>()

    fun getGroupeCoucheList(): List<GroupeCouche> = dsl.selectFrom(GROUPE_COUCHE).orderBy(GROUPE_COUCHE.ORDRE.desc()).fetchInto<GroupeCouche>()

    fun getIcone(idCouche: UUID): ByteArray? =
        dsl.select(COUCHE.ICONE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.ICONE)

    fun getLegende(idCouche: UUID): ByteArray? =
        dsl.select(COUCHE.LEGENDE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.LEGENDE)

    fun upsertGroupeCouche(couche: GroupeCouche): Int = with(dsl.newRecord(GROUPE_COUCHE, couche)) {
        return dsl
            .insertInto(GROUPE_COUCHE)
            .set(this)
            .onConflict()
            .doUpdate()
            .set(this)
            .execute()
    }

    fun upsertCouche(couche: Couche): Int = with(dsl.newRecord(COUCHE, couche)) {
        dsl
            .insertInto(COUCHE)
            .set(this)
            .onConflict()
            .doUpdate()
            .set(COUCHE.GROUPE_COUCHE_ID, couche.coucheGroupeCoucheId)
            .set(COUCHE.LIBELLE, couche.coucheLibelle)
            .set(COUCHE.CODE, couche.coucheCode)
            .set(COUCHE.NOM, couche.coucheNom)
            .set(COUCHE.ORDRE, couche.coucheOrdre)
            .set(COUCHE.SOURCE, couche.coucheSource)
            .set(COUCHE.PROJECTION, couche.coucheProjection)
            .set(COUCHE.CROSS_ORIGIN, couche.coucheCrossOrigin)
            .set(COUCHE.URL, couche.coucheUrl)
            .set(COUCHE.FORMAT, couche.coucheFormat)
            .set(COUCHE.PUBLIC, couche.couchePublic)
            .set(COUCHE.ACTIVE, couche.coucheActive)
            .set(COUCHE.PROXY, couche.coucheProxy)
            .execute()
    }

    fun updateIcone(coucheId: UUID, icone: ByteArray?) =
        dsl.update(COUCHE)
            .set(COUCHE.ICONE, icone)
            .where(COUCHE.ID.eq(coucheId))
            .execute()

    fun updateLegende(coucheId: UUID, legende: ByteArray?) =
        dsl.update(COUCHE)
            .set(COUCHE.LEGENDE, legende)
            .where(COUCHE.ID.eq(coucheId))
            .execute()

    fun removeOldCouche(toKeep: Collection<UUID>): Int = dsl.deleteFrom(COUCHE).where(COUCHE.ID.notIn(toKeep)).execute()

    fun removeOldGroupeCouche(toKeep: Collection<UUID>): Int =
        dsl.deleteFrom(GROUPE_COUCHE).where(GROUPE_COUCHE.ID.notIn(toKeep)).execute()

    fun clearProfilDroit(): Int = dsl.deleteFrom(L_COUCHE_DROIT).execute()

    fun clearModule(): Int = dsl.deleteFrom(L_COUCHE_MODULE).execute()

    fun insertProfilDroit(coucheId: UUID, profilDroitId: UUID): Int =
        dsl.insertInto(L_COUCHE_DROIT)
            .set(L_COUCHE_DROIT.COUCHE_ID, coucheId)
            .set(L_COUCHE_DROIT.PROFIL_DROIT_ID, profilDroitId)
            .execute()

    fun insertModule(coucheId: UUID, moduleType: TypeModule): Int =
        dsl.insertInto(L_COUCHE_MODULE)
            .set(L_COUCHE_MODULE.COUCHE_ID, coucheId)
            .set(L_COUCHE_MODULE.MODULE_TYPE, moduleType)
            .execute()
}
