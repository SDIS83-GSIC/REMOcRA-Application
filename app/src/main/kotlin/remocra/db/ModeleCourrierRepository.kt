package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.ModeleCourrierData
import remocra.data.ModeleCourrierParametreData
import remocra.data.Params
import remocra.data.enums.TypeModuleRapportCourrier
import remocra.db.jooq.remocra.enums.TypeModule
import remocra.db.jooq.remocra.enums.TypeParametreRapportCourrier
import remocra.db.jooq.remocra.tables.pojos.LModeleCourrierGroupeFonctionnalites
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrier
import remocra.db.jooq.remocra.tables.pojos.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_MODELE_COURRIER_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER
import remocra.db.jooq.remocra.tables.references.MODELE_COURRIER_PARAMETRE
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ModeleCourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getByCode(code: String): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.eq(code))
            .fetchSingleInto()

    fun getById(modeleCourrierId: UUID): ModeleCourrier =
        dsl.selectFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .fetchSingleInto()

    fun getAll(utilisateurId: UUID): Collection<ModeleCourrier> =
        dsl.select(*MODELE_COURRIER.fields())
            .from(MODELE_COURRIER)
            .join(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .on(MODELE_COURRIER.ID.eq(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID))
            .join(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
            .join(UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
            .where(UTILISATEUR.ID.eq(utilisateurId))
            .fetchInto()

    /**
     * Retourne les paramètres groupés par courrier
     */
    fun getParametresByModele(): Map<ModeleCourrier, List<ModeleCourrierParametre>> =
        dsl.select(
            *MODELE_COURRIER.fields(),
            *MODELE_COURRIER_PARAMETRE.fields(),
        )
            .from(MODELE_COURRIER)
            .join(MODELE_COURRIER_PARAMETRE)
            .on(MODELE_COURRIER.ID.eq(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID))
            .fetchGroups(ModeleCourrier::class.java, ModeleCourrierParametre::class.java)

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<ModeleCourrierComplet> =
        dsl.selectDistinct(
            MODELE_COURRIER.ID,
            MODELE_COURRIER.CODE,
            MODELE_COURRIER.ACTIF,
            MODELE_COURRIER.PROTECTED,
            MODELE_COURRIER.LIBELLE,
            MODELE_COURRIER.DESCRIPTION,
            MODELE_COURRIER.MODULE,
            multiset(
                selectDistinct(GROUPE_FONCTIONNALITES.LIBELLE)
                    .from(GROUPE_FONCTIONNALITES)
                    .join(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
                    .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
                    .where(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }?.joinToString()
            }.`as`("listeGroupeFonctionnalites"),
        )
            .from(MODELE_COURRIER)
            .leftJoin(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
            .where(params.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(MODELE_COURRIER.MODULE, MODELE_COURRIER.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.selectDistinct(
            MODELE_COURRIER.ID,
        )
            .from(MODELE_COURRIER)
            .leftJoin(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    data class Filter(
        val modeleCourrierCode: String?,
        val modeleCourrierLibelle: String?,
        val modeleCourrierActif: Boolean?,
        val modeleCourrierProtected: Boolean?,
        val modeleCourrierModule: TypeModuleRapportCourrier?,
        val listeGroupeFonctionnalitesId: Collection<UUID>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    modeleCourrierCode?.let { DSL.and(MODELE_COURRIER.CODE.containsIgnoreCase(it)) },
                    modeleCourrierLibelle?.let { DSL.and(MODELE_COURRIER.LIBELLE.containsIgnoreCase(it)) },
                    modeleCourrierActif?.let { DSL.and(MODELE_COURRIER.ACTIF.eq(it)) },
                    modeleCourrierProtected?.let { DSL.and(MODELE_COURRIER.PROTECTED.eq(it)) },
                    modeleCourrierModule?.let { DSL.and(MODELE_COURRIER.MODULE.eq(TypeModule.entries.find { t -> t.name == it.name })) },
                    listeGroupeFonctionnalitesId?.let { DSL.and(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.`in`(it)) },
                ),
            )
    }

    data class Sort(
        val modeleCourrierCode: Int?,
        val modeleCourrierLibelle: Int?,
        val modeleCourrierActif: Int?,
        val modeleCourrierProtected: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            MODELE_COURRIER.CODE.getSortField(modeleCourrierCode),
            MODELE_COURRIER.ACTIF.getSortField(modeleCourrierActif),
            MODELE_COURRIER.LIBELLE.getSortField(modeleCourrierLibelle),
            MODELE_COURRIER.PROTECTED.getSortField(modeleCourrierProtected),
        )
    }

    data class ModeleCourrierComplet(
        val modeleCourrierId: UUID,
        val modeleCourrierActif: Boolean,
        val modeleCourrierCode: String,
        val modeleCourrierLibelle: String,
        val modeleCourrierProtected: Boolean,
        val modeleCourrierDescription: String?,
        val modeleCourrierModule: TypeModuleRapportCourrier,
        val listeGroupeFonctionnalites: String?,
    )

    fun insertModeleCourrier(modeleCourrier: ModeleCourrier) {
        dsl.insertInto(MODELE_COURRIER)
            .set(dsl.newRecord(MODELE_COURRIER, modeleCourrier))
            .execute()
    }

    fun updateModeleCourrier(modeleCourrier: ModeleCourrier) {
        dsl.update(MODELE_COURRIER)
            .set(dsl.newRecord(MODELE_COURRIER, modeleCourrier))
            .where(MODELE_COURRIER.ID.eq(modeleCourrier.modeleCourrierId))
            .execute()
    }

    fun insertLModeleCourrierGroupeFonctionnalites(lModeleCourrierGroupeFonctionnalites: LModeleCourrierGroupeFonctionnalites) =
        dsl.insertInto(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .set(dsl.newRecord(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES, lModeleCourrierGroupeFonctionnalites))
            .execute()

    fun upsertModeleCourrierParametre(modeleCourrierParametre: ModeleCourrierParametre) =
        dsl.insertInto(MODELE_COURRIER_PARAMETRE)
            .set(dsl.newRecord(MODELE_COURRIER_PARAMETRE, modeleCourrierParametre))
            .onConflict(MODELE_COURRIER_PARAMETRE.ID)
            .doUpdate()
            .set(dsl.newRecord(MODELE_COURRIER_PARAMETRE, modeleCourrierParametre))
            .execute()

    /**
     * Vérifie s'il existe déjà un élément avec ce *code*. En modification, on regarde si le code existe pour un autre élément que lui-même
     */
    fun checkCodeExists(modeleCourrierCode: String, modeleCourrierId: UUID?) = dsl.fetchExists(
        dsl.select(MODELE_COURRIER.CODE)
            .from(MODELE_COURRIER)
            .where(MODELE_COURRIER.CODE.equalIgnoreCase(modeleCourrierCode))
            .and(MODELE_COURRIER.ID.notEqual(modeleCourrierId)),
    )

    fun getModeleCourrier(modeleCourrierId: UUID): ModeleCourrierData =
        dsl.select(
            MODELE_COURRIER.ID,
            MODELE_COURRIER.CODE,
            MODELE_COURRIER.ACTIF,
            MODELE_COURRIER.LIBELLE,
            MODELE_COURRIER.SOURCE_SQL,
            MODELE_COURRIER.DESCRIPTION,
            MODELE_COURRIER.MODULE,
            MODELE_COURRIER.CORPS_EMAIL,
            MODELE_COURRIER.OBJET_EMAIL,
            multiset(
                selectDistinct(GROUPE_FONCTIONNALITES.ID)
                    .from(GROUPE_FONCTIONNALITES)
                    .join(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
                    .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(GROUPE_FONCTIONNALITES.ID))
                    .where(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    r.value1()
                }
            }.`as`("listeGroupeFonctionnalitesId"),
            DOCUMENT.ID,
            DOCUMENT.NOM_FICHIER,
            DOCUMENT.REPERTOIRE,
            multiset(
                selectDistinct(
                    MODELE_COURRIER_PARAMETRE.CODE,
                    MODELE_COURRIER_PARAMETRE.LIBELLE,
                    MODELE_COURRIER_PARAMETRE.DESCRIPTION,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL_ID,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL_LIBELLE,
                    MODELE_COURRIER_PARAMETRE.VALEUR_DEFAUT,
                    MODELE_COURRIER_PARAMETRE.IS_REQUIRED,
                    MODELE_COURRIER_PARAMETRE.TYPE,
                    MODELE_COURRIER_PARAMETRE.ORDRE,
                    MODELE_COURRIER_PARAMETRE.ID,
                )
                    .from(MODELE_COURRIER_PARAMETRE)
                    .where(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
                    .orderBy(MODELE_COURRIER_PARAMETRE.ORDRE),
            ).`as`("listeModeleCourrierParametre").convertFrom { record ->
                record.map {
                    ModeleCourrierParametreData(
                        modeleCourrierParametreId = it.value11().let { it as UUID },
                        modeleCourrierParametreCode = it.value1() as String,
                        modeleCourrierParametreLibelle = it.value2() as String,
                        modeleCourrierParametreDescription = it.value3(),
                        modeleCourrierParametreSourceSql = it.value4(),
                        modeleCourrierParametreSourceSqlId = it.value5(),
                        modeleCourrierParametreSourceSqlLibelle = it.value6(),
                        modeleCourrierParametreValeurDefaut = it.value7(),
                        modeleCourrierParametreIsRequired = it.value8() as Boolean,
                        modeleCourrierParametreType = it.value9() as TypeParametreRapportCourrier,
                        modeleCourrierParametreOrdre = it.value10() as Int,
                    )
                }
            },
        )
            .from(MODELE_COURRIER)
            .join(DOCUMENT)
            .on(DOCUMENT.ID.eq(MODELE_COURRIER.DOCUMENT_ID))
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .fetchSingleInto()

    fun deleteLGroupeFonctionnalites(modeleCourrierId: UUID) =
        dsl.deleteFrom(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
            .where(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(modeleCourrierId))
            .execute()

    fun deleteModeleCourrierParametre(modeleCourrierId: UUID) =
        dsl.deleteFrom(MODELE_COURRIER_PARAMETRE)
            .where(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID.eq(modeleCourrierId))
            .execute()

    fun deleteModeleCourrier(modeleCourrierId: UUID) =
        dsl.deleteFrom(MODELE_COURRIER)
            .where(MODELE_COURRIER.ID.eq(modeleCourrierId))
            .execute()

    fun getListeModeleCourrier(
        utilisateurId: UUID,
        isSuperAdmin: Boolean,
        typeModule: TypeModule,
    ): Collection<ModeleCourrierGenere> =
        dsl.selectDistinct(
            MODELE_COURRIER.ID,
            MODELE_COURRIER.LIBELLE,
            MODELE_COURRIER.DESCRIPTION,
            multiset(
                selectDistinct(
                    MODELE_COURRIER_PARAMETRE.CODE,
                    MODELE_COURRIER_PARAMETRE.LIBELLE,
                    MODELE_COURRIER_PARAMETRE.DESCRIPTION,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL_ID,
                    MODELE_COURRIER_PARAMETRE.SOURCE_SQL_LIBELLE,
                    MODELE_COURRIER_PARAMETRE.VALEUR_DEFAUT,
                    MODELE_COURRIER_PARAMETRE.IS_REQUIRED,
                    MODELE_COURRIER_PARAMETRE.TYPE,
                    MODELE_COURRIER_PARAMETRE.ORDRE,
                    MODELE_COURRIER_PARAMETRE.ID,
                )
                    .from(MODELE_COURRIER_PARAMETRE)
                    .where(MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
                    .orderBy(MODELE_COURRIER_PARAMETRE.ORDRE),
            ).`as`("listeModeleCourrierParametre").convertFrom { record ->
                record.map {
                    ModeleCourrierParametreData(
                        modeleCourrierParametreId = it.value11().let { it as UUID },
                        modeleCourrierParametreCode = it.value1() as String,
                        modeleCourrierParametreLibelle = it.value2() as String,
                        modeleCourrierParametreDescription = it.value3(),
                        modeleCourrierParametreSourceSql = it.value4(),
                        modeleCourrierParametreSourceSqlId = it.value5(),
                        modeleCourrierParametreSourceSqlLibelle = it.value6(),
                        modeleCourrierParametreValeurDefaut = it.value7(),
                        modeleCourrierParametreIsRequired = it.value8() as Boolean,
                        modeleCourrierParametreType = it.value9() as TypeParametreRapportCourrier,
                        modeleCourrierParametreOrdre = it.value10() as Int,
                    )
                }
            },
        )
            .from(
                isSuperAdmin.let {
                    if (it) {
                        MODELE_COURRIER
                    } else {
                        MODELE_COURRIER.join(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
                            .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(MODELE_COURRIER.ID))
                            .join(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
                            .on(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
                            .join(UTILISATEUR)
                            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
                            .join(ORGANISME)
                            .on(
                                ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID)
                                    .and(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID)),
                            )
                    }
                },
            )
            .where(MODELE_COURRIER.ACTIF.isTrue)
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    UTILISATEUR.ID.eq(utilisateurId),
                    isSuperAdmin = isSuperAdmin,
                ),
            )
            .and(MODELE_COURRIER.MODULE.eq(typeModule))
            .fetchInto()

    data class ModeleCourrierGenere(
        val modeleCourrierId: UUID,
        val modeleCourrierLibelle: String,
        val modeleCourrierDescription: String?,
        val listeModeleCourrierParametre: Collection<ModeleCourrierParametreData>,
    )

    fun executeRequeteSql(requete: String): MutableMap<String, Any?>? =
        dsl.fetchOne(requete)?.intoMap()

    fun checkGroupeFonctionnalites(modeleCourrierId: UUID, utilisateurId: UUID) =
        dsl.fetchExists(
            dsl.select(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID)
                .from(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES)
                .join(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES)
                .on(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID.eq(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.GROUPE_FONCTIONNALITES_ID))
                .join(ORGANISME)
                .on(ORGANISME.PROFIL_ORGANISME_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_ORGANISME_ID))
                .join(UTILISATEUR)
                .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(L_PROFIL_UTILISATEUR_ORGANISME_GROUPE_FONCTIONNALITES.PROFIL_UTILISATEUR_ID))
                .where(UTILISATEUR.ID.eq(utilisateurId))
                .and(L_MODELE_COURRIER_GROUPE_FONCTIONNALITES.MODELE_COURRIER_ID.eq(modeleCourrierId)),
        )
}
