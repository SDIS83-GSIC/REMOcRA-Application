package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
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
    fun getCoucheMap(module: TypeModule, profilDroit: ProfilDroit?): Map<UUID, List<Couche>> =
        dsl.selectDistinct(*COUCHE.fields())
            .from(COUCHE)
            .leftJoin(L_COUCHE_DROIT).on(L_COUCHE_DROIT.COUCHE_ID.eq(COUCHE.ID))
            .join(L_COUCHE_MODULE).on(L_COUCHE_MODULE.COUCHE_ID.eq(COUCHE.ID).and(L_COUCHE_MODULE.MODULE_TYPE.eq(module)))
            .where(COUCHE.PUBLIC.isTrue)
            .or(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(profilDroit?.profilDroitId))
            .fetchInto<Couche>().groupBy { it.coucheGroupeCoucheId }

    fun getProfilDroitList(coucheId: UUID): List<ProfilDroit> =
        dsl.select(*PROFIL_DROIT.fields())
            .from(PROFIL_DROIT)
            .join(L_COUCHE_DROIT).on(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
            .where(L_COUCHE_DROIT.COUCHE_ID.eq(coucheId))
            .fetchInto<ProfilDroit>()

    fun getCoucheList(groupeCoucheId: UUID): List<Couche> = dsl.selectFrom(COUCHE).where(COUCHE.GROUPE_COUCHE_ID.eq(groupeCoucheId)).fetchInto<Couche>()

    fun getGroupeCoucheList(): List<GroupeCouche> = dsl.selectFrom(GROUPE_COUCHE).fetchInto<GroupeCouche>()

    fun getIcone(idCouche: UUID): ByteArray? = dsl.select(COUCHE.ICONE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.ICONE)

    fun getLegende(idCouche: UUID): ByteArray? = dsl.select(COUCHE.LEGENDE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.LEGENDE)

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
            .set(this)
            .execute()
    }

    fun removeOldCouche(toKeep: Collection<UUID>): Int = dsl.deleteFrom(COUCHE).where(COUCHE.ID.notIn(toKeep)).execute()

    fun removeOldGroupeCouche(toKeep: Collection<UUID>): Int = dsl.deleteFrom(GROUPE_COUCHE).where(GROUPE_COUCHE.ID.notIn(toKeep)).execute()

    fun clearProfilDroit(): Int = dsl.deleteFrom(L_COUCHE_DROIT).execute()

    fun insertProfilDroit(coucheId: UUID, profilDroitId: UUID): Int =
        dsl.insertInto(L_COUCHE_DROIT)
            .set(L_COUCHE_DROIT.COUCHE_ID, coucheId)
            .set(L_COUCHE_DROIT.PROFIL_DROIT_ID, profilDroitId)
            .execute()
}
