package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.IdLibelleRapportPersonnalise
import remocra.data.Params
import remocra.data.RapportPersonnaliseData
import remocra.data.RapportPersonnaliseParametreData
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.LRapportPersonnaliseProfilDroit
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnalise
import remocra.db.jooq.remocra.tables.pojos.RapportPersonnaliseParametre
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.L_RAPPORT_PERSONNALISE_PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_DROIT
import remocra.db.jooq.remocra.tables.references.RAPPORT_PERSONNALISE
import remocra.db.jooq.remocra.tables.references.RAPPORT_PERSONNALISE_PARAMETRE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class RapportPersonnaliseRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<RapportPersonnaliseComplet> =
        dsl.selectDistinct(
            RAPPORT_PERSONNALISE.ID,
            RAPPORT_PERSONNALISE.CODE,
            RAPPORT_PERSONNALISE.ACTIF,
            RAPPORT_PERSONNALISE.PROTECTED,
            RAPPORT_PERSONNALISE.LIBELLE,
            RAPPORT_PERSONNALISE.CHAMP_GEOMETRIE,
            RAPPORT_PERSONNALISE.DESCRIPTION,
            RAPPORT_PERSONNALISE.MODULE,
            multiset(
                selectDistinct(PROFIL_DROIT.LIBELLE)
                    .from(PROFIL_DROIT)
                    .join(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
                    .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                    .where(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("listeProfilDroit"),
        )
            .from(RAPPORT_PERSONNALISE)
            .leftJoin(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
            .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(RAPPORT_PERSONNALISE.MODULE, RAPPORT_PERSONNALISE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.selectDistinct(
            RAPPORT_PERSONNALISE.ID,
        )
            .from(RAPPORT_PERSONNALISE)
            .leftJoin(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
            .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val rapportPersonnaliseCode: String?,
        val rapportPersonnaliseLibelle: String?,
        val rapportPersonnaliseActif: Boolean?,
        val rapportPersonnaliseProtected: Boolean?,
        val rapportPersonnaliseChampGeometrie: Boolean?,
        val rapportPersonnaliseModule: TypeModuleRapportCourrier?,
        val listeProfilDroitId: Collection<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    rapportPersonnaliseCode?.let { DSL.and(RAPPORT_PERSONNALISE.CODE.containsIgnoreCaseUnaccent(it)) },
                    rapportPersonnaliseLibelle?.let { DSL.and(RAPPORT_PERSONNALISE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                    rapportPersonnaliseActif?.let { DSL.and(RAPPORT_PERSONNALISE.ACTIF.eq(it)) },
                    rapportPersonnaliseProtected?.let { DSL.and(RAPPORT_PERSONNALISE.PROTECTED.eq(it)) },
                    rapportPersonnaliseModule?.let { DSL.and(RAPPORT_PERSONNALISE.MODULE.eq(TypeModule.entries.find { t -> t.name == it.name })) },
                    rapportPersonnaliseChampGeometrie?.let {
                        if (it) {
                            DSL.and(RAPPORT_PERSONNALISE.CHAMP_GEOMETRIE.isNull)
                        } else {
                            DSL.and(RAPPORT_PERSONNALISE.CHAMP_GEOMETRIE.isNotNull)
                        }
                    },
                    listeProfilDroitId?.let { DSL.and(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.PROFIL_DROIT_ID.`in`(it)) },
                ),
            )
    }

    data class Sort(
        val rapportPersonnaliseCode: Int?,
        val rapportPersonnaliseLibelle: Int?,
        val rapportPersonnaliseActif: Int?,
        val rapportPersonnaliseProtected: Int?,
        val rapportPersonnaliseChampGeometrie: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            RAPPORT_PERSONNALISE.CODE.getSortField(rapportPersonnaliseCode),
            RAPPORT_PERSONNALISE.LIBELLE.getSortField(rapportPersonnaliseLibelle),
            RAPPORT_PERSONNALISE.ACTIF.getSortField(rapportPersonnaliseActif),
            RAPPORT_PERSONNALISE.PROTECTED.getSortField(rapportPersonnaliseProtected),
            RAPPORT_PERSONNALISE.CHAMP_GEOMETRIE.getSortField(rapportPersonnaliseChampGeometrie),
        )
    }

    data class RapportPersonnaliseComplet(
        val rapportPersonnaliseId: UUID,
        val rapportPersonnaliseActif: Boolean,
        val rapportPersonnaliseCode: String,
        val rapportPersonnaliseLibelle: String,
        val rapportPersonnaliseProtected: Boolean,
        val rapportPersonnaliseChampGeometrie: String?,
        val rapportPersonnaliseDescription: String?,
        val rapportPersonnaliseModule: TypeModuleRapportCourrier,
        val listeProfilDroit: String?,
    )

    fun getRapportPersonnalise(rapportPersonnaliseId: UUID): RapportPersonnaliseData =
        dsl.selectDistinct(
            RAPPORT_PERSONNALISE.ID,
            RAPPORT_PERSONNALISE.CODE,
            RAPPORT_PERSONNALISE.ACTIF,
            RAPPORT_PERSONNALISE.LIBELLE,
            RAPPORT_PERSONNALISE.SOURCE_SQL,
            RAPPORT_PERSONNALISE.DESCRIPTION,
            RAPPORT_PERSONNALISE.CHAMP_GEOMETRIE,
            RAPPORT_PERSONNALISE.MODULE,
            multiset(
                selectDistinct(PROFIL_DROIT.ID)
                    .from(PROFIL_DROIT)
                    .join(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
                    .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.PROFIL_DROIT_ID.eq(PROFIL_DROIT.ID))
                    .where(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }
            }.`as`("listeProfilDroitId"),
            multiset(
                selectDistinct(
                    RAPPORT_PERSONNALISE_PARAMETRE.CODE,
                    RAPPORT_PERSONNALISE_PARAMETRE.LIBELLE,
                    RAPPORT_PERSONNALISE_PARAMETRE.DESCRIPTION,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL_ID,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL_LIBELLE,
                    RAPPORT_PERSONNALISE_PARAMETRE.VALEUR_DEFAUT,
                    RAPPORT_PERSONNALISE_PARAMETRE.IS_REQUIRED,
                    RAPPORT_PERSONNALISE_PARAMETRE.TYPE,
                    RAPPORT_PERSONNALISE_PARAMETRE.ORDRE,
                    RAPPORT_PERSONNALISE_PARAMETRE.ID,
                )
                    .from(RAPPORT_PERSONNALISE_PARAMETRE)
                    .where(RAPPORT_PERSONNALISE_PARAMETRE.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID))
                    .orderBy(RAPPORT_PERSONNALISE_PARAMETRE.ORDRE),
            ).`as`("listeRapportPersonnaliseParametre").convertFrom { record ->
                record.map {
                    RapportPersonnaliseParametreData(
                        rapportPersonnaliseParametreId = it.value11().let { it as UUID },
                        rapportPersonnaliseParametreCode = it.value1() as String,
                        rapportPersonnaliseParametreLibelle = it.value2() as String,
                        rapportPersonnaliseParametreDescription = it.value3(),
                        rapportPersonnaliseParametreSourceSql = it.value4(),
                        rapportPersonnaliseParametreSourceSqlId = it.value5(),
                        rapportPersonnaliseParametreSourceSqlLibelle = it.value6(),
                        rapportPersonnaliseParametreValeurDefaut = it.value7(),
                        rapportPersonnaliseParametreIsRequired = it.value8() as Boolean,
                        rapportPersonnaliseParametreType = it.value9() as TypeParametreRapportPersonnalise,
                        rapportPersonnaliseParametreOrdre = it.value10() as Int,
                    )
                }
            },
        )
            .from(RAPPORT_PERSONNALISE)
            .where(RAPPORT_PERSONNALISE.ID.eq(rapportPersonnaliseId))
            .fetchSingleInto()

    fun executeSqlParametre(requete: String): List<IdLibelleRapportPersonnalise> =
        dsl.fetch(requete).into(IdLibelleRapportPersonnalise::class.java)

    fun executeSqlRapport(requete: String) =
        dsl.fetch(requete)

    fun insertRapportPersonnalise(rapportPersonnalise: RapportPersonnalise) =
        dsl.insertInto(RAPPORT_PERSONNALISE)
            .set(dsl.newRecord(RAPPORT_PERSONNALISE, rapportPersonnalise))
            .execute()

    fun updateRapportPersonnalise(rapportPersonnalise: RapportPersonnalise) =
        dsl.update(RAPPORT_PERSONNALISE)
            .set(dsl.newRecord(RAPPORT_PERSONNALISE, rapportPersonnalise))
            .where(RAPPORT_PERSONNALISE.ID.eq(rapportPersonnalise.rapportPersonnaliseId))
            .execute()

    fun deleteLRapportPersonnaliseProfilDroit(rapportPersonnaliseId: UUID) =
        dsl.deleteFrom(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
            .where(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(rapportPersonnaliseId))
            .execute()

    fun deleteRapportPersonnaliseParametre(rapportPersonnaliseId: UUID) =
        dsl.deleteFrom(RAPPORT_PERSONNALISE_PARAMETRE)
            .where(RAPPORT_PERSONNALISE_PARAMETRE.RAPPORT_PERSONNALISE_ID.eq(rapportPersonnaliseId))
            .execute()

    fun deleteRapportPersonnalise(rapportPersonnaliseId: UUID) =
        dsl.deleteFrom(RAPPORT_PERSONNALISE)
            .where(RAPPORT_PERSONNALISE.ID.eq(rapportPersonnaliseId))
            .execute()

    fun insertLRapportPersonnaliseProfilDroit(lRapportPersonnaliseProfilDroit: LRapportPersonnaliseProfilDroit) =
        dsl.insertInto(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
            .set(dsl.newRecord(L_RAPPORT_PERSONNALISE_PROFIL_DROIT, lRapportPersonnaliseProfilDroit))
            .execute()

    fun upsertRapportPersonnaliseParametre(rapportPersonnaliseParametre: RapportPersonnaliseParametre) =
        dsl.insertInto(RAPPORT_PERSONNALISE_PARAMETRE)
            .set(dsl.newRecord(RAPPORT_PERSONNALISE_PARAMETRE, rapportPersonnaliseParametre))
            .onConflict(RAPPORT_PERSONNALISE_PARAMETRE.ID)
            .doUpdate()
            .set(dsl.newRecord(RAPPORT_PERSONNALISE_PARAMETRE, rapportPersonnaliseParametre))
            .execute()

    /**
     * Vérifie s'il existe déjà un élément avec ce *code*. En modification, on regarde si le code existe pour un autre élément que lui-même
     */
    fun checkCodeExists(rapportPersonnaliseCode: String, rapportPersonnaliseId: UUID?) = dsl.fetchExists(
        dsl.select(RAPPORT_PERSONNALISE.CODE)
            .from(RAPPORT_PERSONNALISE)
            .where(RAPPORT_PERSONNALISE.CODE.equalIgnoreCase(rapportPersonnaliseCode))
            .and(RAPPORT_PERSONNALISE.ID.notEqual(rapportPersonnaliseId)),
    )

    fun getRapportPersonnalisePojo(rapportPersonnaliseId: UUID): RapportPersonnalise =
        dsl.selectFrom(RAPPORT_PERSONNALISE)
            .where(RAPPORT_PERSONNALISE.ID.eq(rapportPersonnaliseId))
            .fetchSingleInto()

    fun getRapportPersonnaliseParametrePojo(rapportPersonnaliseId: UUID): Collection<RapportPersonnaliseParametre> =
        dsl.selectFrom(RAPPORT_PERSONNALISE_PARAMETRE)
            .where(RAPPORT_PERSONNALISE_PARAMETRE.RAPPORT_PERSONNALISE_ID.eq(rapportPersonnaliseId))
            .fetchInto()

    fun getListeRapportPersonnalise(
        utilisateurId: UUID,
        isSuperAdmin: Boolean,
    ): Collection<RapportPersonnaliseGenere> =
        dsl.select(
            RAPPORT_PERSONNALISE.ID,
            RAPPORT_PERSONNALISE.LIBELLE,
            RAPPORT_PERSONNALISE.DESCRIPTION,
            multiset(
                selectDistinct(
                    RAPPORT_PERSONNALISE_PARAMETRE.CODE,
                    RAPPORT_PERSONNALISE_PARAMETRE.LIBELLE,
                    RAPPORT_PERSONNALISE_PARAMETRE.DESCRIPTION,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL_ID,
                    RAPPORT_PERSONNALISE_PARAMETRE.SOURCE_SQL_LIBELLE,
                    RAPPORT_PERSONNALISE_PARAMETRE.VALEUR_DEFAUT,
                    RAPPORT_PERSONNALISE_PARAMETRE.IS_REQUIRED,
                    RAPPORT_PERSONNALISE_PARAMETRE.TYPE,
                    RAPPORT_PERSONNALISE_PARAMETRE.ORDRE,
                    RAPPORT_PERSONNALISE_PARAMETRE.ID,
                )
                    .from(RAPPORT_PERSONNALISE_PARAMETRE)
                    .where(RAPPORT_PERSONNALISE_PARAMETRE.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID))
                    .orderBy(RAPPORT_PERSONNALISE_PARAMETRE.ORDRE),
            ).`as`("listeRapportPersonnaliseParametre").convertFrom { record ->
                record.map {
                    RapportPersonnaliseParametreData(
                        rapportPersonnaliseParametreId = it.value11().let { it as UUID },
                        rapportPersonnaliseParametreCode = it.value1() as String,
                        rapportPersonnaliseParametreLibelle = it.value2() as String,
                        rapportPersonnaliseParametreDescription = it.value3(),
                        rapportPersonnaliseParametreSourceSql = it.value4(),
                        rapportPersonnaliseParametreSourceSqlId = it.value5(),
                        rapportPersonnaliseParametreSourceSqlLibelle = it.value6(),
                        rapportPersonnaliseParametreValeurDefaut = it.value7(),
                        rapportPersonnaliseParametreIsRequired = it.value8() as Boolean,
                        rapportPersonnaliseParametreType = it.value9() as TypeParametreRapportPersonnalise,
                        rapportPersonnaliseParametreOrdre = it.value10() as Int,
                    )
                }
            },
        )
            .from(RAPPORT_PERSONNALISE)
            .join(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
            .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(RAPPORT_PERSONNALISE.ID))
            .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
            .on(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.PROFIL_DROIT_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
            .join(ORGANISME)
            .on(
                ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID)
                    .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID)),
            )
            .where(RAPPORT_PERSONNALISE.ACTIF.isTrue)
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    UTILISATEUR.ID.eq(utilisateurId),
                    isSuperAdmin = isSuperAdmin,
                ),
            )
            .fetchInto()

    data class RapportPersonnaliseGenere(
        val rapportPersonnaliseId: UUID,
        val rapportPersonnaliseLibelle: String,
        val rapportPersonnaliseDescription: String?,
        val listeRapportPersonnaliseParametre: Collection<RapportPersonnaliseParametreData>,
    )

    fun checkDroitRapportPersonnalise(utilisateurId: UUID, rapportPersonnaliseId: UUID) =
        dsl.fetchExists(
            dsl.select(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID)
                .from(L_RAPPORT_PERSONNALISE_PROFIL_DROIT)
                .join(L_PROFIL_UTILISATEUR_ORGANISME_DROIT)
                .on(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_DROIT_ID.eq(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.PROFIL_DROIT_ID))
                .join(ORGANISME)
                .on(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
                .join(UTILISATEUR)
                .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID))
                .where(UTILISATEUR.ID.eq(utilisateurId))
                .and(L_RAPPORT_PERSONNALISE_PROFIL_DROIT.RAPPORT_PERSONNALISE_ID.eq(rapportPersonnaliseId)),
        )

    fun getSqlRequete(rapportPersonnaliseId: UUID): String =
        dsl.select(RAPPORT_PERSONNALISE.SOURCE_SQL)
            .from(RAPPORT_PERSONNALISE)
            .where(RAPPORT_PERSONNALISE.ID.eq(rapportPersonnaliseId))
            .fetchSingleInto()
}
