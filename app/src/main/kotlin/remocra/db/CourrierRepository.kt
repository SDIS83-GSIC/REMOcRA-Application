package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.auth.UserInfo
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.L_COURRIER_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_COURRIER
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.time.ZonedDateTime
import java.util.UUID

class CourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        private val expediteurAlias: Table<*> = ORGANISME.`as`("EXPEDITEUR")
    }

    fun setAccuse(courrierId: UUID, userId: UUID) = dsl.update(L_COURRIER_UTILISATEUR)
        .set(L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION, dateUtils.now())
        .where(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(userId))
        .and(L_COURRIER_UTILISATEUR.COURRIER_ID.eq(courrierId))
        .execute()

    fun getDocumentByCourrier(courrierId: UUID): Document =
        dsl.select(*DOCUMENT.fields())
            .from(COURRIER)
            .join(DOCUMENT)
            .on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .where(COURRIER.ID.eq(courrierId))
            .fetchSingleInto()

    fun getCourrierCompletWithThematique(
        listeThematiqueId: Collection<UUID>,
        userInfo: UserInfo,
        params: Params<Filter, Sort>?,
    ): Collection<CourrierComplet> {
        return dsl.selectDistinct(
//            Info courrier
            COURRIER.ID,
            COURRIER.REFERENCE,
            expediteurAlias.field(ORGANISME.LIBELLE)!!.`as`("courrierExpediteur"),
            COURRIER.OBJET,
//            Infos Document
            DOCUMENT.DATE,
            COURRIER.DOCUMENT_ID,
//        Infos Destinataire
            multiset(
                selectDistinct(UTILISATEUR.EMAIL, L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION)
                    .from(UTILISATEUR)
                    .join(L_COURRIER_UTILISATEUR)
                    .on(L_COURRIER_UTILISATEUR.COURRIER_ID.eq(COURRIER.ID))
                    .and(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(UTILISATEUR.ID))
                    .where(
                        params?.filterBy?.accuse?.let { L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION.isNotNull().eq(it) }
                            ?: DSL.noCondition(),
                    ),
            ).convertFrom { record ->
                record?.map { r ->
                    Destinataire(
                        email = r.value1()!!,
                        accuse = r.value2() != null,
                    )
                }
            }.`as`("emailDestinataire"),
        )
            .from(COURRIER)
            .join(DOCUMENT)
            .on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .join(L_THEMATIQUE_COURRIER)
            .on(L_THEMATIQUE_COURRIER.COURRIER_ID.eq(COURRIER.ID))
            .leftJoin(L_COURRIER_UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.COURRIER_ID.eq(COURRIER.ID))
            .join(UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID))
            .join(expediteurAlias)
            .on(COURRIER.EXPEDITEUR.eq(expediteurAlias.field(ORGANISME.ID)))
            .where(L_THEMATIQUE_COURRIER.THEMATIQUE_ID.`in`(listeThematiqueId))
            /**
             Si superAdmin ou que tu es expéditeur ou que tu es destinataire ou que l'organisme d'un des destinataires
             t'es affilié
             **/
            .and(
                repositoryUtils
                    .checkIsSuperAdminOrCondition(
                        UTILISATEUR.ID.eq(userInfo.utilisateurId)
                            .or(
                                ORGANISME.ID
                                    .`in`(userInfo.affiliatedOrganismeIds),
                            ),
                        userInfo.isSuperAdmin,
                    ),
            )
            .and(params?.filterBy?.toCondition(expediteurAlias) ?: DSL.noCondition())
            .orderBy(
                params?.sortBy?.toCondition()
                    .takeIf { !it.isNullOrEmpty() } ?: listOf(DOCUMENT.DATE.desc()),
            )
            .limit(params?.limit)
            .fetchInto()
    }
    fun countCourrierCompletWithThematique(
        listeThematiqueId: Collection<UUID>,
        userInfo: UserInfo,
        params: Params<Filter, Sort>?,
    ): Int =
        dsl.selectDistinct(
//            Info courrier
            COURRIER.ID,
        )
            .from(COURRIER)
            .join(DOCUMENT)
            .on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .join(L_THEMATIQUE_COURRIER)
            .on(L_THEMATIQUE_COURRIER.COURRIER_ID.eq(COURRIER.ID))
            .leftJoin(L_COURRIER_UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.COURRIER_ID.eq(COURRIER.ID))
            .join(UTILISATEUR)
            .on(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(UTILISATEUR.ID))
            .leftJoin(ORGANISME)
            .on(ORGANISME.ID.eq(UTILISATEUR.ORGANISME_ID))
            .join(expediteurAlias)
            .on(COURRIER.EXPEDITEUR.eq(expediteurAlias.field(ORGANISME.ID)))
            .where(L_THEMATIQUE_COURRIER.THEMATIQUE_ID.`in`(listeThematiqueId))
            /**
             Si superAdmin ou que tu es expéditeur ou que tu es destinataire ou que l'organisme d'un des destinataires
             t'es affilié
             **/
            .and(
                repositoryUtils
                    .checkIsSuperAdminOrCondition(
                        UTILISATEUR.ID.eq(userInfo.utilisateurId)
                            .or(
                                ORGANISME.ID
                                    .`in`(userInfo.affiliatedOrganismeIds),
                            ),
                        userInfo.isSuperAdmin,
                    ),
            )
            .and(params?.filterBy?.toCondition(expediteurAlias) ?: DSL.noCondition())
            .count()

    data class Filter(
        val courrierObjet: String?,
        val courrierReference: String?,
        val courrierExpediteur: String?,
        val emailDestinataire: List<UUID>?,
        val accuse: Boolean?,
    ) {
        fun toCondition(expediteurAlias: Table<*>): Condition =
            DSL.and(
                listOfNotNull(
                    courrierObjet?.let { DSL.and(COURRIER.OBJET.containsIgnoreCaseUnaccent(it)) },
                    courrierReference?.let { DSL.and(COURRIER.REFERENCE.containsIgnoreCaseUnaccent(it)) },
                    courrierExpediteur?.let { expediteurAlias.field(ORGANISME.LIBELLE)!!.containsIgnoreCaseUnaccent(it) },
                    emailDestinataire?.let { DSL.and(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.`in`(it)) },
                    accuse?.let {
                        if (accuse) {
                            DSL.and(L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION.isNotNull)
                        } else {
                            DSL.and(L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION.isNull)
                        }
                    },
                ),
            )
    }

    data class Sort(
        val objet: Int?,
        val date: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            COURRIER.OBJET.getSortField(objet),
            DOCUMENT.DATE.getSortField(date),
        )
    }

    data class CourrierComplet(
        val courrierId: UUID,
        val courrierDocumentId: UUID,
        val courrierReference: String,
        val courrierObjet: String,
        val courrierExpediteur: String?,
        val documentDate: ZonedDateTime,
        val emailDestinataire: Collection<Destinataire>,
        val courrierAccuse: Boolean,
    )

    data class Destinataire(
        val email: String,
        val accuse: Boolean,
    )
}
