package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.DSL
import remocra.data.tracabilite.Search
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.historique.tables.pojos.Tracabilite
import remocra.db.jooq.historique.tables.references.TRACABILITE
import remocra.db.jooq.remocra.enums.Disponibilite
import java.time.ZonedDateTime
import java.util.UUID

class TracabiliteRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insertTracabilite(
        typeOperation: TypeOperation,
        typeObjet: TypeObjet,
        objetId: UUID,
        objetData: JSONB,
        auteurId: UUID,
        auteurData: JSONB,
        date: ZonedDateTime,
    ) =
        dsl.insertInto(TRACABILITE)
            .set(TRACABILITE.ID, UUID.randomUUID())
            .set(TRACABILITE.TYPE_OPERATION, typeOperation)
            .set(TRACABILITE.DATE, date)
            .set(TRACABILITE.OBJET_ID, objetId)
            .set(TRACABILITE.TYPE_OBJET, typeObjet)
            .set(TRACABILITE.OBJET_DATA, objetData)
            .set(TRACABILITE.AUTEUR_ID, auteurId)
            .set(TRACABILITE.AUTEUR_DATA, auteurData)
            .execute()

    /**
     * Retourne tous les éléments de traçabilité de PEI (+visites) à partir d'un instant donné
     */
    fun getTracabilitePeiAndVisiteSince(moment: ZonedDateTime): List<Tracabilite> {
        return dsl.selectFrom(TRACABILITE).where(TRACABILITE.TYPE_OBJET.`in`(listOf(TypeObjet.PEI, TypeObjet.VISITE)))
            .and(TRACABILITE.DATE.ge(moment))
            .fetchInto()
    }

    /**
     * Retourne tous les éléments de traçabilité de PEI à partir d'un instant donnée
     * @param moment ZonedDateTime :
     */
    fun getTracabilitePeiSince(moment: ZonedDateTime): List<Tracabilite> =
        dsl.selectFrom(TRACABILITE)
            .where(TRACABILITE.TYPE_OBJET.eq(TypeObjet.PEI))
            .and(TRACABILITE.DATE.ge(moment))
            .orderBy(TRACABILITE.OBJET_ID, TRACABILITE.DATE.desc())
            .fetchInto()

    fun getTracabilitePei(peiId: UUID): Collection<Tracabilite> =
        dsl.selectFrom(TRACABILITE)
            .where(TRACABILITE.TYPE_OBJET.eq(TypeObjet.PEI))
            .and(TRACABILITE.OBJET_ID.eq(peiId))
            .orderBy(TRACABILITE.DATE.desc())
            .fetchInto()

    fun getPreviousPeiTracaEvent(peiId: UUID, maxDate: ZonedDateTime): Tracabilite? =
        dsl.selectFrom(TRACABILITE)
            .where(TRACABILITE.TYPE_OBJET.eq(TypeObjet.PEI))
            .and(TRACABILITE.DATE.lt(maxDate)) // Strictement inférieur à
            .and(TRACABILITE.OBJET_ID.eq(peiId))
            .orderBy(TRACABILITE.DATE.desc())
            .fetchAnyInto()

    fun searchTracabilite(search: Search): List<Tracabilite> =
        dsl.selectFrom(TRACABILITE)
            .where(
                DSL.and(
                    listOfNotNull(
                        search.typeObjet?.let { TRACABILITE.TYPE_OBJET.eq(it) },
                        search.typeUtilisateur?.let {
                            DSL.jsonbGetAttributeAsText(TRACABILITE.AUTEUR_DATA, "typeSourceModification")
                                .eq(it.name)
                        },
                        search.utilisateur?.let {
                            val cndNom = DSL.jsonbGetAttributeAsText(TRACABILITE.AUTEUR_DATA, "nom")
                                .containsIgnoreCase(it)
                            val cndPrenom = DSL.jsonbGetAttributeAsText(TRACABILITE.AUTEUR_DATA, "prenom")
                                .containsIgnoreCase(it)
                            val cndEmail = DSL.jsonbGetAttributeAsText(TRACABILITE.AUTEUR_DATA, "email")
                                .containsIgnoreCase(it)
                            cndNom.or(cndPrenom).or(cndEmail)
                        },
                        search.typeOperation?.let { TRACABILITE.TYPE_OPERATION.eq(it) },
                        search.debut?.let {
                            TRACABILITE.DATE.ge(dateUtils.getMoment(it))
                        },
                        search.fin?.let {
                            TRACABILITE.DATE.le(dateUtils.getMoment(it))
                        },
                        search.objetId?.let { TRACABILITE.OBJET_ID.eq(it) },
                    ),
                ),
            )
            .orderBy(TRACABILITE.DATE.desc())
            .fetchInto()

    fun getLastDateByPei(listePeiId: List<UUID>): Map<UUID?, ZonedDateTime?> =
        dsl.select(TRACABILITE.OBJET_ID, DSL.max(TRACABILITE.DATE)).from(TRACABILITE)
            .where(
                TRACABILITE.OBJET_ID.`in`(listePeiId),
            )
            .and(TRACABILITE.TYPE_OBJET.eq(TypeObjet.PEI))
            .groupBy(TRACABILITE.OBJET_ID)
            .fetchMap(TRACABILITE.OBJET_ID, DSL.max(TRACABILITE.DATE))

    fun getIndispoTempTracabilite(listeIndispoTempId: List<UUID>): Collection<Tracabilite> =
        dsl.selectFrom(TRACABILITE)
            .where(TRACABILITE.TYPE_OBJET.eq(TypeObjet.INDISPONIBILITE_TEMPORAIRE))
            .and(TRACABILITE.OBJET_ID.`in`(listeIndispoTempId))
            .fetchInto()

    /**
     * Permet de retourner les PEI qui ont été disponibles entre aujourd'hui et aujourd'hui - nbJours
     */
    fun getPeiIdDisponibles(nbJours: Int): Collection<UUID> =
        dsl.select(TRACABILITE.OBJET_ID)
            .from(TRACABILITE)
            .where(TRACABILITE.TYPE_OBJET.eq(TypeObjet.PEI))
            .and(TRACABILITE.DATE.between(dateUtils.now().minusDays(nbJours.toLong()), dateUtils.now()))
            .and(
                DSL.jsonbGetAttributeAsText(TRACABILITE.OBJET_DATA, "peiDisponibiliteTerrestre")
                    .eq(Disponibilite.DISPONIBLE.literal).or(TRACABILITE.TYPE_OPERATION.eq(TypeOperation.INSERT)),
            )
            .fetchInto()
}
