package remocra.db

import jakarta.inject.Inject
import org.jooq.CommonTableExpression
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record3
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.jooq.impl.SQLDataType
import remocra.auth.WrappedUserInfo
import remocra.data.DestinataireData
import remocra.data.Params
import remocra.data.TypeDestinataire
import remocra.db.jooq.remocra.tables.pojos.Courrier
import remocra.db.jooq.remocra.tables.pojos.Document
import remocra.db.jooq.remocra.tables.pojos.LCourrierContactGestionnaire
import remocra.db.jooq.remocra.tables.pojos.LCourrierContactOrganisme
import remocra.db.jooq.remocra.tables.pojos.LCourrierOrganisme
import remocra.db.jooq.remocra.tables.pojos.LCourrierUtilisateur
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.DOCUMENT
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_COURRIER_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_COURRIER_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_COURRIER_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_COURRIER_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_COURRIER
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import remocra.utils.DateUtils
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.math.absoluteValue

class CourrierRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    companion object {
        private val expediteurAlias: Table<*> = ORGANISME.`as`("EXPEDITEUR")

        private val destinataireNomCte = name("destinataire")
        private val destinataireCte =
            destinataireNomCte.fields("courrier_id", "destinataire_email", "accuse_reception").`as`(
                DSL.select(
                    L_COURRIER_UTILISATEUR.COURRIER_ID,
                    UTILISATEUR.EMAIL,
                    DSL.`when`(L_COURRIER_UTILISATEUR.ACCUSE_RECEPTION.isNotNull(), DSL.`val`(true))
                        .otherwise(DSL.`val`(false)).`as`("accuse_reception"),
                )
                    .from(L_COURRIER_UTILISATEUR)
                    .join(UTILISATEUR).on(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(UTILISATEUR.ID))
                    .union(
                        DSL.select(
                            L_COURRIER_ORGANISME.COURRIER_ID,
                            ORGANISME.EMAIL_CONTACT,
                            DSL.`val`<Boolean>(null).`as`("accuse_reception"),
                        )
                            .from(L_COURRIER_ORGANISME)
                            .join(ORGANISME).on(L_COURRIER_ORGANISME.ORGANISME_ID.eq(ORGANISME.ID)),
                    )
                    .union(
                        DSL.select(
                            L_COURRIER_CONTACT_GESTIONNAIRE.COURRIER_ID,
                            CONTACT.EMAIL,
                            DSL.`val`<Boolean>(null).`as`("accuse_reception"),
                        )
                            .from(L_COURRIER_CONTACT_GESTIONNAIRE)
                            .join(CONTACT).on(L_COURRIER_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID)),
                    )
                    .union(
                        DSL.select(
                            L_COURRIER_CONTACT_ORGANISME.COURRIER_ID,
                            CONTACT.EMAIL,
                            DSL.`val`<Boolean>(null).`as`("accuse_reception"),
                        )
                            .from(L_COURRIER_CONTACT_ORGANISME)
                            .join(CONTACT).on(L_COURRIER_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID)),
                    ),
            )
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
        userInfo: WrappedUserInfo,
        params: Params<Filter, Sort>?,
    ): Collection<CourrierComplet> =
        dsl.with(destinataireCte).selectDistinct(
            COURRIER.ID,
            COURRIER.REFERENCE,
            expediteurAlias.field(ORGANISME.LIBELLE)!!.`as`("courrierExpediteur"),
            COURRIER.OBJET,
            DOCUMENT.DATE,
            COURRIER.DOCUMENT_ID,
            multiset(
                dsl.select(
                    destinataireCte.field("courrier_id", UUID::class.java),
                    destinataireCte.field("destinataire_email", String::class.java),
                    destinataireCte.field("accuse_reception", Boolean::class.java),
                )
                    .from(destinataireCte)
                    .where(destinataireCte.field("courrier_id", UUID::class.java)!!.eq(COURRIER.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    Destinataire(
                        email = r.get("destinataire_email", String::class.java),
                        accuse = r.get("accuse_reception") as Boolean?,
                    )
                }
            }.`as`("emailDestinataire"),
        )
            .from(COURRIER)
            .join(expediteurAlias).on(COURRIER.EXPEDITEUR.eq(expediteurAlias.field(ORGANISME.ID)))
            .join(DOCUMENT).on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .join(L_THEMATIQUE_COURRIER).on(COURRIER.ID.eq(L_THEMATIQUE_COURRIER.COURRIER_ID))
            .leftJoin(L_COURRIER_UTILISATEUR).on(COURRIER.ID.eq(L_COURRIER_UTILISATEUR.COURRIER_ID))
            .join(destinataireCte).on(destinataireCte.field("courrier_id", UUID::class.java)!!.eq(COURRIER.ID))
            .where(L_THEMATIQUE_COURRIER.THEMATIQUE_ID.`in`(listeThematiqueId))
            /**
             Si superAdmin
             ou que tu es expéditeur == Mon organisme est l'expéditeur => userInfo.organismeId == COURRIER.EXPEDITEUR
             ou que tu es destinataire => L_COURRIER_UTILISATEUR.UTILISATEUR_ID = userInfo.utilisateurId
             ou que l'organisme d'un des destinataires t'est affilié => COURRIER.EXPEDITEUR.`in`(userInfo.affiliatedOrganismeIds)
             **/
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COURRIER.EXPEDITEUR.eq(userInfo.organismeId)
                        .or(COURRIER.EXPEDITEUR.`in`(userInfo.affiliatedOrganismeIds))
                        .or(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(userInfo.utilisateurId)),
                    userInfo.isSuperAdmin,
                ),
            )
            .and(params?.filterBy?.toCondition(expediteurAlias, destinataireCte, dateUtils) ?: DSL.noCondition())
            .orderBy(
                params?.sortBy?.toCondition()
                    .takeIf { !it.isNullOrEmpty() } ?: listOf(DOCUMENT.DATE.desc()),
            )
            .limit(params?.limit)
            .offset(params?.offset)
            .fetchInto()

    fun countCourrierCompletWithThematique(
        listeThematiqueId: Collection<UUID>,
        userInfo: WrappedUserInfo,
        params: Params<Filter, Sort>?,
    ): Int =
        dsl.with(destinataireCte).selectDistinct(
            COURRIER.ID,
        )
            .from(COURRIER)
            .join(expediteurAlias).on(COURRIER.EXPEDITEUR.eq(expediteurAlias.field(ORGANISME.ID)))
            .join(DOCUMENT).on(COURRIER.DOCUMENT_ID.eq(DOCUMENT.ID))
            .join(L_THEMATIQUE_COURRIER).on(COURRIER.ID.eq(L_THEMATIQUE_COURRIER.COURRIER_ID))
            .leftJoin(L_COURRIER_UTILISATEUR).on(COURRIER.ID.eq(L_COURRIER_UTILISATEUR.COURRIER_ID))
            .join(destinataireCte).on(destinataireCte.field("courrier_id", UUID::class.java)!!.eq(COURRIER.ID))
            .where(L_THEMATIQUE_COURRIER.THEMATIQUE_ID.`in`(listeThematiqueId))
            .and(
                repositoryUtils.checkIsSuperAdminOrCondition(
                    COURRIER.EXPEDITEUR.eq(userInfo.organismeId)
                        .or(COURRIER.EXPEDITEUR.`in`(userInfo.affiliatedOrganismeIds))
                        .or(L_COURRIER_UTILISATEUR.COURRIER_ID.`in`(DSL.select(L_COURRIER_UTILISATEUR.COURRIER_ID).from(L_COURRIER_UTILISATEUR).where(L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(userInfo.utilisateurId)))),
                    userInfo.isSuperAdmin,
                ),
            )
            .and(params?.filterBy?.toCondition(expediteurAlias, destinataireCte, dateUtils) ?: DSL.noCondition())
            .count()

    data class Filter(
        val courrierObjet: String?,
        val courrierReference: String?,
        val courrierExpediteur: String?,
        val emailDestinataire: String?,
        val accuse: Boolean?,
        val documentDate: IntervalleDateProche?,
    ) {
        enum class IntervalleDateProche {
            INFERIEUR_1_JOUR,
            INFERIEUR_1_SEMAINE,
            INFERIEUR_1_MOIS,
            INFERIEUR_3_MOIS,
            INFERIEUR_6_MOIS,
        }

        fun toCondition(expediteurAlias: Table<*>, destinataireCte: CommonTableExpression<Record3<UUID?, String?, Boolean?>?>, dateUtils: DateUtils): Condition =
            DSL.and(
                listOfNotNull(
                    courrierObjet?.let { DSL.and(COURRIER.OBJET.containsIgnoreCaseUnaccent(it)) },
                    courrierReference?.let { DSL.and(COURRIER.REFERENCE.containsIgnoreCaseUnaccent(it)) },
                    courrierExpediteur?.let { expediteurAlias.field(ORGANISME.LIBELLE)!!.containsIgnoreCaseUnaccent(it) },
                    emailDestinataire?.let { DSL.and(destinataireCte.field("destinataire_email", String::class.java)!!.containsIgnoreCaseUnaccent(it)) },
                    accuse?.let {
                        if (accuse) {
                            DSL.and(destinataireCte.field("accuse_reception", Boolean::class.java)?.isTrue)
                        } else {
                            DSL.and(destinataireCte.field("accuse_reception", Boolean::class.java)?.isFalse)
                        }
                    },
                    documentDate?.let {
                        when (it) {
                            IntervalleDateProche.INFERIEUR_1_JOUR -> DSL.and(DOCUMENT.DATE.ge(dateUtils.now().minusDays(1)))
                            IntervalleDateProche.INFERIEUR_1_SEMAINE -> DSL.and(DOCUMENT.DATE.ge(dateUtils.now().minusWeeks(1)))
                            IntervalleDateProche.INFERIEUR_1_MOIS -> DSL.and(DOCUMENT.DATE.ge(dateUtils.now().minusMonths(1)))
                            IntervalleDateProche.INFERIEUR_3_MOIS -> DSL.and(DOCUMENT.DATE.ge(dateUtils.now().minusMonths(3)))
                            IntervalleDateProche.INFERIEUR_6_MOIS -> DSL.and(DOCUMENT.DATE.ge(dateUtils.now().minusMonths(6)))
                        }
                    },
                ),
            )
    }

    data class Sort(
        val courrierObjet: Int?,
        val courrierReference: Int?,
        val courrierExpediteur: Int?,
        val documentDate: Int?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            courrierObjet?.let { "courrierObjet" to it },
            courrierReference?.let { "courrierReference" to it },
            courrierExpediteur?.let { "courrierExpediteur" to it },
            documentDate?.let { "documentDate" to it },
        )
        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "courrierObjet" -> COURRIER.OBJET.getSortField(pair.second)
                "courrierReference" -> COURRIER.REFERENCE.getSortField(pair.second)
                "courrierExpediteur" -> expediteurAlias.field(ORGANISME.LIBELLE)?.getSortField(pair.second)
                "documentDate" -> DOCUMENT.DATE.getSortField(pair.second)
                else -> null
            }
        }
    }

    data class CourrierComplet(
        val courrierId: UUID,
        val courrierDocumentId: UUID,
        val courrierReference: String,
        val courrierObjet: String,
        val courrierExpediteur: String?,
        val documentDate: ZonedDateTime,
        val emailDestinataire: Collection<Destinataire>,
    )

    data class Destinataire(
        val email: String,
        val accuse: Boolean?,
    )

    fun getAllDestinataires(filterBy: FilterDestinataire?, sortBy: SortDestinataire?, limit: Int?, offset: Int?): Collection<DestinataireData> {
        val nomCte = name("LISTE_DESTINATAIRE")
        val cte = nomCte.fields(
            "destinataireId",
            "nomDestinataire",
            "emailDestinataire",
            "fonctionDestinataire",
            "typeDestinataire",
        )
            .`as`(getRequestDestinataire())

        return dsl.with(cte).selectFrom(table(nomCte))
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .orderBy(
                sortBy?.toCondition().takeIf { it.isNullOrEmpty() }
                    ?: listOf(
                        field(name("LISTE_DESTINATAIRE", "typeDestinataire")),
                        field(name("LISTE_DESTINATAIRE", "nomDestinataire")),
                    ),
            )
            .limit(limit)
            .offset(offset)
            .fetchInto()
    }

    fun countDestinataire(): Int =
        dsl.fetchCount(getRequestDestinataire())

    private fun getRequestDestinataire() =
        dsl.select(
            UTILISATEUR.ID.`as`("destinataireId"),
            DSL.concat(UTILISATEUR.NOM, DSL.value(" "), UTILISATEUR.PRENOM)
                .`as`("nomDestinataire"),
            UTILISATEUR.EMAIL.`as`("emailDestinataire"),
            PROFIL_UTILISATEUR.LIBELLE.`as`("fonctionDestinataire"),
            DSL.value(TypeDestinataire.UTILISATEUR.libelle).`as`("typeDestinataire"),
        )
            .from(UTILISATEUR)
            .join(PROFIL_UTILISATEUR)
            .on(UTILISATEUR.PROFIL_UTILISATEUR_ID.eq(PROFIL_UTILISATEUR.ID))
            .where(UTILISATEUR.ACTIF.isTrue)
            .and(UTILISATEUR.CAN_BE_NOTIFIED.isTrue)
            .and(UTILISATEUR.EMAIL.isNotNull)
            .union(
                dsl.select(
                    ORGANISME.ID.`as`("destinataireId"),
                    ORGANISME.LIBELLE.`as`("nomDestinataire"),
                    ORGANISME.EMAIL_CONTACT.`as`("emailDestinataire"),
                    PROFIL_ORGANISME.LIBELLE.`as`("fonctionDestinataire"),
                    DSL.value(TypeDestinataire.ORGANISME.libelle).`as`("typeDestinataire"),
                )
                    .from(ORGANISME)
                    .join(PROFIL_ORGANISME)
                    .on(ORGANISME.PROFIL_ORGANISME_ID.eq(PROFIL_ORGANISME.ID))
                    .where(ORGANISME.ACTIF.isTrue)
                    .and(ORGANISME.EMAIL_CONTACT.isNotNull),
            )
            .union(
                dsl.select(
                    CONTACT.ID.`as`("destinataireId"),
                    DSL.concat(CONTACT.NOM, DSL.value(" "), CONTACT.PRENOM)
                        .`as`("nomDestinataire"),
                    CONTACT.EMAIL.`as`("emailDestinataire"),
                    FONCTION_CONTACT.LIBELLE.`as`("fonctionDestinataire"),
                    DSL.value(TypeDestinataire.CONTACT_ORGANISME.libelle).`as`("typeDestinataire"),
                )
                    .from(CONTACT)
                    .leftJoin(FONCTION_CONTACT)
                    .on(FONCTION_CONTACT.ID.eq(CONTACT.FONCTION_CONTACT_ID))
                    .join(L_CONTACT_ORGANISME)
                    .on(L_CONTACT_ORGANISME.CONTACT_ID.eq(CONTACT.ID))
                    .where(CONTACT.ACTIF.isTrue)
                    .and(CONTACT.EMAIL.isNotNull),
            )
            .union(
                dsl.select(
                    CONTACT.ID.`as`("destinataireId"),
                    DSL.concat(
                        CONTACT.NOM,
                        DSL.value(" "),
                        CONTACT.PRENOM,
                        DSL.value(" ("),
                        GESTIONNAIRE.LIBELLE,
                        DSL.value(")"),
                    )
                        .`as`("nomDestinataire"),
                    CONTACT.EMAIL.`as`("emailDestinataire"),
                    FONCTION_CONTACT.LIBELLE.`as`("fonctionDestinataire"),
                    DSL.value(TypeDestinataire.CONTACT_GESTIONNAIRE.libelle).`as`("typeDestinataire"),
                )
                    .from(CONTACT)
                    .leftJoin(FONCTION_CONTACT)
                    .on(FONCTION_CONTACT.ID.eq(CONTACT.FONCTION_CONTACT_ID))
                    .join(L_CONTACT_GESTIONNAIRE)
                    .on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
                    .join(GESTIONNAIRE)
                    .on(GESTIONNAIRE.ID.eq(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID))
                    .where(CONTACT.ACTIF.isTrue)
                    .and(CONTACT.EMAIL.isNotNull),
            )

    data class FilterDestinataire(
        val nomDestinataire: String?,
        val emailDestinataire: String?,
        val fonctionDestinataire: String?,
        val listeTypeDestinataire: List<TypeDestinataire>?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    nomDestinataire?.let { DSL.and(field(name("LISTE_DESTINATAIRE", "nomDestinataire"), SQLDataType.VARCHAR).containsIgnoreCaseUnaccent(it)) },
                    emailDestinataire?.let { DSL.and(field(name("LISTE_DESTINATAIRE", "emailDestinataire"), SQLDataType.VARCHAR).containsIgnoreCaseUnaccent(it)) },
                    fonctionDestinataire?.let { DSL.and(field(name("LISTE_DESTINATAIRE", "fonctionDestinataire"), SQLDataType.VARCHAR).containsIgnoreCaseUnaccent(it)) },
                    listeTypeDestinataire?.let { DSL.and(field(name("LISTE_DESTINATAIRE", "typeDestinataire"), SQLDataType.VARCHAR).`in`(it.map { it.libelle })) },

                ),
            )
    }

    data class SortDestinataire(
        val nomDestinataire: Int?,
        val emailDestinataire: Int?,
        val fonctionDestinataire: Int?,
        val typeDestinataire: Int?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            field(name("LISTE_DESTINATAIRE", "nomDestinataire"), SQLDataType.VARCHAR).getSortField(nomDestinataire),
            field(name("LISTE_DESTINATAIRE", "emailDestinataire"), SQLDataType.VARCHAR).getSortField(emailDestinataire),
            field(name("LISTE_DESTINATAIRE", "fonctionDestinataire"), SQLDataType.VARCHAR).getSortField(fonctionDestinataire),
            field(name("LISTE_DESTINATAIRE", "typeDestinataire"), SQLDataType.VARCHAR).getSortField(typeDestinataire),

        )
    }

    fun insertCourrier(courrier: Courrier) =
        dsl.insertInto(COURRIER)
            .set(dsl.newRecord(COURRIER, courrier))
            .execute()

    fun insertLCourrierUtilisateur(lCourrierUtilisateur: LCourrierUtilisateur) =
        dsl.insertInto(L_COURRIER_UTILISATEUR)
            .set(dsl.newRecord(L_COURRIER_UTILISATEUR, lCourrierUtilisateur))
            .execute()

    fun insertLCourrierOrganisme(lCourrierOrganisme: LCourrierOrganisme) =
        dsl.insertInto(L_COURRIER_ORGANISME)
            .set(dsl.newRecord(L_COURRIER_ORGANISME, lCourrierOrganisme))
            .execute()

    fun insertLCourrierContactOrganisme(lCourrierContactOrganisme: LCourrierContactOrganisme) =
        dsl.insertInto(L_COURRIER_CONTACT_ORGANISME)
            .set(dsl.newRecord(L_COURRIER_CONTACT_ORGANISME, lCourrierContactOrganisme))
            .execute()

    fun insertLCourrierContactGestionnaire(lCourrierContactGestionnaire: LCourrierContactGestionnaire) =
        dsl.insertInto(L_COURRIER_CONTACT_GESTIONNAIRE)
            .set(dsl.newRecord(L_COURRIER_CONTACT_GESTIONNAIRE, lCourrierContactGestionnaire))
            .execute()
}
