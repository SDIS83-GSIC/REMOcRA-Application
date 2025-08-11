package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import remocra.data.GlobalData
import remocra.data.Params
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_CONTACT_ROLE
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT
import remocra.db.jooq.remocra.tables.references.SITE
import remocra.tasks.Destinataire
import java.util.UUID
import kotlin.math.absoluteValue

class GestionnaireRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun getAll(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(GESTIONNAIRE.ID.`as`("id"), GESTIONNAIRE.CODE.`as`("code"), GESTIONNAIRE.LIBELLE.`as`("libelle"))
            .from(GESTIONNAIRE)
            .where(GESTIONNAIRE.ACTIF)
            .orderBy(GESTIONNAIRE.LIBELLE)
            .fetchInto()

    fun getAllForMobile(): Collection<Gestionnaire> =
        dsl.selectFrom(GESTIONNAIRE)
            .where(GESTIONNAIRE.ACTIF)
            .orderBy(GESTIONNAIRE.LIBELLE)
            .fetchInto()

    fun getAllForAdmin(params: Params<Filter, Sort>): Collection<GestionnaireWithHasContact> =
        dsl.select(
            *GESTIONNAIRE.fields(),
            DSL.field(
                DSL.exists(
                    dsl.select(L_CONTACT_GESTIONNAIRE.CONTACT_ID)
                        .from(
                            L_CONTACT_GESTIONNAIRE,
                        )
                        .where(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID.eq(GESTIONNAIRE.ID)),
                ),
            ).`as`("hasContact"),
        )
            .from(GESTIONNAIRE)
            .where(params.filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(params.sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(GESTIONNAIRE.LIBELLE))
            .limit(params.limit)
            .offset(params.offset)
            .fetchInto()

    data class GestionnaireWithHasContact(
        val gestionnaireId: UUID,
        val gestionnaireActif: Boolean,
        val gestionnaireCode: String,
        val gestionnaireLibelle: String,
        val hasContact: Boolean,
    )

    fun countAllForAdmin(filterBy: Filter?) =
        dsl.select(GESTIONNAIRE.ID)
            .from(GESTIONNAIRE)
            .where(filterBy?.toCondition() ?: DSL.noCondition())
            .count()

    fun getById(gestionnaireId: UUID): Gestionnaire =
        dsl.selectFrom(GESTIONNAIRE).where(GESTIONNAIRE.ID.eq(gestionnaireId)).fetchSingleInto()

    data class Filter(
        val gestionnaireCode: String?,
        val gestionnaireLibelle: String?,
        val gestionnaireActif: Boolean?,

    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    gestionnaireCode?.let { DSL.and(GESTIONNAIRE.CODE.containsUnaccent(it)) },
                    gestionnaireLibelle?.let { DSL.and(GESTIONNAIRE.LIBELLE.containsUnaccent(it)) },
                    gestionnaireActif?.let { DSL.and(GESTIONNAIRE.ACTIF.eq(it)) },
                ),
            )
    }

    data class Sort(
        val gestionnaireCode: Int?,
        val gestionnaireLibelle: Int?,
        val gestionnaireActif: Int?,
    ) {

        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            gestionnaireCode?.let { "gestionnaireCode" to it },
            gestionnaireLibelle?.let { "gestionnaireLibelle" to it },
            gestionnaireActif?.let { "gestionnaireActif" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "gestionnaireCode" -> GESTIONNAIRE.CODE.getSortField(pair.second)
                "gestionnaireLibelle" -> GESTIONNAIRE.LIBELLE.getSortField(pair.second)
                "gestionnaireActif" -> GESTIONNAIRE.ACTIF.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun upsertGestionnaire(gestionnaire: Gestionnaire) = with(dsl.newRecord(GESTIONNAIRE, gestionnaire)) {
        dsl.insertInto(GESTIONNAIRE)
            .set(this)
            .onConflict()
            .doUpdate()
            .set(this)
            .execute()
    }

    fun deleteGestionnaire(gestionnaireId: UUID) =
        dsl.delete(GESTIONNAIRE)
            .where(GESTIONNAIRE.ID.eq(gestionnaireId))
            .execute()

    fun gestionnaireUsedInPei(gestionnaireId: UUID) =
        dsl.fetchExists(
            dsl.select(PEI.ID)
                .from(PEI)
                .where(PEI.GESTIONNAIRE_ID.eq(gestionnaireId)),
        )

    fun gestionnaireUsedInSite(gestionnaireId: UUID) =
        dsl.fetchExists(
            dsl.select(SITE.ID)
                .from(SITE)
                .where(SITE.GESTIONNAIRE_ID.eq(gestionnaireId)),
        )

    fun checkCodeExists(code: String, id: UUID?) =
        dsl.fetchExists(
            dsl.select(GESTIONNAIRE.CODE)
                .from(GESTIONNAIRE)
                .where(GESTIONNAIRE.CODE.eq(code))
                .and(id?.let { DSL.and(GESTIONNAIRE.ID.notEqual(id)) }),
        )

    fun checkExists(gestionnaireId: UUID) =
        dsl.fetchExists(
            dsl.select(GESTIONNAIRE.CODE)
                .from(GESTIONNAIRE)
                .where(GESTIONNAIRE.ID.eq(gestionnaireId)),
        )

    fun getDestinataireContactGestionnaire(
        listePeiId: List<UUID>,
        contactRole: String,
    ): Map<Destinataire, List<UUID?>> =
        dsl.select(
            PEI.ID,
            CONTACT.ID,
            CONTACT.CIVILITE,
            FONCTION_CONTACT.LIBELLE,
            CONTACT.NOM,
            CONTACT.PRENOM,
            CONTACT.EMAIL,
        )
            .from(GESTIONNAIRE)
            .join(L_CONTACT_GESTIONNAIRE).on(GESTIONNAIRE.ID.eq(L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID))
            .join(CONTACT).on(L_CONTACT_GESTIONNAIRE.CONTACT_ID.eq(CONTACT.ID))
            .join(PEI).on(GESTIONNAIRE.ID.eq(PEI.GESTIONNAIRE_ID))
            .join(L_CONTACT_ROLE).on(CONTACT.ID.eq(L_CONTACT_ROLE.CONTACT_ID))
            .join(ROLE_CONTACT).on(L_CONTACT_ROLE.ROLE_ID.eq(ROLE_CONTACT.ID))
            .leftJoin(FONCTION_CONTACT).on(CONTACT.FONCTION_CONTACT_ID.eq(FONCTION_CONTACT.ID))
            .where(PEI.ID.`in`(listePeiId))
            .and(ROLE_CONTACT.CODE.eq(contactRole))
            .and(CONTACT.EMAIL.isNotNull)
            .fetchGroups(
                { record ->
                    Destinataire(
                        destinataireId = record.get(CONTACT.ID),
                        destinataireCivilite = record.get(CONTACT.CIVILITE),
                        destinataireFonction = record.get(FONCTION_CONTACT.LIBELLE),
                        destinataireNom = record.get(CONTACT.NOM),
                        destinatairePrenom = record.get(CONTACT.PRENOM),
                        destinataireEmail = record.get(CONTACT.EMAIL)!!,
                    )
                },
                { record ->
                    record.get(PEI.ID)
                },
            )
}
