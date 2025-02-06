package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.pojos.LPermisCadastreParcelle
import remocra.db.jooq.remocra.tables.pojos.Permis
import remocra.db.jooq.remocra.tables.references.L_PERMIS_CADASTRE_PARCELLE
import remocra.db.jooq.remocra.tables.references.PERMIS
import remocra.db.jooq.remocra.tables.references.TYPE_PERMIS_AVIS
import remocra.db.jooq.remocra.tables.references.TYPE_PERMIS_INTERSERVICE

class PermisRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getAvis(): Collection<GlobalData.IdCodeLibellePprifData> =
        dsl.select(TYPE_PERMIS_AVIS.ID.`as`("id"), TYPE_PERMIS_AVIS.CODE.`as`("code"), TYPE_PERMIS_AVIS.LIBELLE.`as`("libelle"), TYPE_PERMIS_AVIS.PPRIF.`as`("pprif"))
            .from(TYPE_PERMIS_AVIS)
            .where(TYPE_PERMIS_AVIS.ACTIF)
            .fetchInto()

    fun getInterservice(): Collection<GlobalData.IdCodeLibellePprifData> =
        dsl.select(TYPE_PERMIS_INTERSERVICE.ID.`as`("id"), TYPE_PERMIS_INTERSERVICE.CODE.`as`("code"), TYPE_PERMIS_INTERSERVICE.LIBELLE.`as`("libelle"), TYPE_PERMIS_INTERSERVICE.PPRIF.`as`("pprif"))
            .from(TYPE_PERMIS_INTERSERVICE)
            .where(TYPE_PERMIS_INTERSERVICE.ACTIF)
            .fetchInto()

    fun insertPermis(permis: Permis) =
        dsl.insertInto(PERMIS).set(dsl.newRecord(PERMIS, permis)).execute()

    fun batchInsertPermisParcelle(listePermisParcelle: List<LPermisCadastreParcelle>) =
        dsl.batch(
            listePermisParcelle.map {
                DSL.insertInto(L_PERMIS_CADASTRE_PARCELLE).set(
                    dsl.newRecord(
                        L_PERMIS_CADASTRE_PARCELLE,
                        it,
                    ),
                )
            },
        ).execute()
}
