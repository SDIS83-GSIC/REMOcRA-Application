package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.Couche
import remocra.db.jooq.remocra.tables.pojos.GroupeCouche
import remocra.db.jooq.remocra.tables.pojos.ProfilDroit
import remocra.db.jooq.remocra.tables.references.COUCHE
import remocra.db.jooq.remocra.tables.references.GROUPE_COUCHE
import remocra.db.jooq.remocra.tables.references.L_COUCHE_DROIT
import java.util.UUID

class CoucheRepository @Inject constructor(private val dsl: DSLContext) {
    fun getCoucheMap(profilDroit: ProfilDroit?): Map<UUID, List<Couche>> =
        dsl.select(*COUCHE.fields())
            .from(COUCHE)
            .leftJoin(L_COUCHE_DROIT).on(L_COUCHE_DROIT.COUCHE_ID.eq(COUCHE.ID))
            .where(COUCHE.PUBLIC.isTrue)
            .or(L_COUCHE_DROIT.PROFIL_DROIT_ID.eq(profilDroit?.profilDroitId))
            .fetchInto<Couche>().groupBy { it.coucheGroupeCoucheId }

    fun getGroupeCoucheList(): List<GroupeCouche> = dsl.selectFrom(GROUPE_COUCHE).fetchInto<GroupeCouche>()

    fun getIcone(idCouche: UUID): ByteArray? = dsl.select(COUCHE.ICONE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.ICONE)

    fun getLegende(idCouche: UUID): ByteArray? = dsl.select(COUCHE.LEGENDE).from(COUCHE).where(COUCHE.ID.eq(idCouche)).fetchOne(COUCHE.LEGENDE)
}
