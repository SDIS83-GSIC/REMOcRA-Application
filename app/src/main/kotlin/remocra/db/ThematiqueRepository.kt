package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.auth.UserInfo
import remocra.data.DocumentCourrierData
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.DocumentHabilitable
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_COURRIER_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.L_PROFIL_DROIT_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_COURRIER
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID

class ThematiqueRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {
    fun getAll(actif: Boolean? = null): List<GlobalData.IdCodeLibelleData> =
        dsl.select(
            THEMATIQUE.ID.`as`("id"),
            THEMATIQUE.CODE.`as`("code"),
            THEMATIQUE.LIBELLE.`as`("libelle"),
        )
            .from(THEMATIQUE)
            .where(actif?.let { THEMATIQUE.ACTIF.eq(it) } ?: DSL.noCondition())
            .orderBy(THEMATIQUE.LIBELLE)
            .fetchInto()

    fun getDocumentHabilitableWithThematique(
        listeThematiqueId: Collection<UUID>,
        limit: Int?,
        profilDroitId: UUID?,
        isSuperAdmin: Boolean = false,
        params: Params<Filter, Sort>?,
    ): Collection<DocumentHabilitable> =
        dsl
            .selectDistinct(
                DOCUMENT_HABILITABLE.ID,
                DOCUMENT_HABILITABLE.LIBELLE,
                DOCUMENT_HABILITABLE.DATE_MAJ,
                DOCUMENT_HABILITABLE.DESCRIPTION,
                DOCUMENT.ID,
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(THEMATIQUE).on(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.eq(THEMATIQUE.ID))
            .where(THEMATIQUE.ACTIF.isTrue)
            .and(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.`in`(listeThematiqueId))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    L_PROFIL_DROIT_DOCUMENT_HABILITABLE.PROFIL_DROIT_ID.eq(profilDroitId),
                    isSuperAdmin,
                ),
            )
            .and(params?.filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                params?.sortBy?.toCondition()
                    .takeIf { !it.isNullOrEmpty() } ?: listOf(DOCUMENT_HABILITABLE.DATE_MAJ.desc()),
            )
            .limit(params?.limit ?: limit)
            .fetchInto()

    fun countDocumentHabilitableWithThematique(
        listeThematiqueId: Collection<UUID>,
        profilDroitId: UUID?,
        isSuperAdmin: Boolean = false,
        params: Params<Filter, Sort>?,
    ): Int =
        dsl
            .selectDistinct(
                DOCUMENT_HABILITABLE.ID,
            )
            .from(DOCUMENT)
            .join(DOCUMENT_HABILITABLE)
            .on(DOCUMENT_HABILITABLE.DOCUMENT_ID.eq(DOCUMENT.ID))
            .leftJoin(L_THEMATIQUE_DOCUMENT_HABILITABLE)
            .on(L_THEMATIQUE_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .leftJoin(L_PROFIL_DROIT_DOCUMENT_HABILITABLE)
            .on(L_PROFIL_DROIT_DOCUMENT_HABILITABLE.DOCUMENT_HABILITABLE_ID.eq(DOCUMENT_HABILITABLE.ID))
            .where(L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID.`in`(listeThematiqueId))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    L_PROFIL_DROIT_DOCUMENT_HABILITABLE.PROFIL_DROIT_ID.eq(profilDroitId),
                    isSuperAdmin,
                ),
            )
            .and(params?.filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    fun getCourrierWithThematiqueForAccueil(
        listeThematiqueId: Collection<UUID>,
        limit: Int?,
        userInfo: UserInfo,
    ): Collection<DocumentCourrierData> =
        // Pour l'accueil pas besoin d'info courrier, mais vraiment juste document
        // Mais on parle bien du document venant d'un courrier.
        dsl.selectDistinct(
            DOCUMENT.ID.`as`("id"),
            DOCUMENT.NOM_FICHIER.`as`("libelle"),
            DOCUMENT.DATE.`as`("date"),
        )
            .from(COURRIER)
            .join(DOCUMENT)
            .on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .join(L_THEMATIQUE_COURRIER)
            .on(L_THEMATIQUE_COURRIER.COURRIER_ID.eq(COURRIER.ID))
            .leftJoin(L_COURRIER_UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.COURRIER_ID.eq(COURRIER.ID))
            /**Permet d'aller chercher les courriers**/
            .leftJoin(UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .or(COURRIER.EXPEDITEUR.eq(UTILISATEUR.ID))
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID))
            .where(L_THEMATIQUE_COURRIER.THEMATIQUE_ID.`in`(listeThematiqueId))
            /**
             Si superAdmin ou que tu es expéditeur ou que tu es destinataire ou que l'organisme d'un des destinataires
             t'es affilié
             **/
            .and(
                repositoryUtils
                    .checkIsSuperAdminOrCondition(
                        UTILISATEUR.ID.eq(userInfo.utilisateurId).or(
                            ORGANISME.ID
                                .`in`(userInfo.affiliatedOrganismeIds),
                        ),
                        userInfo.isSuperAdmin,
                    ),
            )
            .orderBy(
                listOf(DOCUMENT.DATE.desc()),
            )
            .limit(limit)
            .fetchInto()

    data class Filter(
        val libelle: String?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    libelle?.let { DSL.and(DOCUMENT_HABILITABLE.LIBELLE.containsIgnoreCaseUnaccent(it)) },
                ),
            )
    }

    data class Sort(
        val libelle: Int?,
        val date: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            DOCUMENT_HABILITABLE.LIBELLE.getSortField(libelle),
            DOCUMENT_HABILITABLE.DATE_MAJ.getSortField(date),
        )
    }
}
