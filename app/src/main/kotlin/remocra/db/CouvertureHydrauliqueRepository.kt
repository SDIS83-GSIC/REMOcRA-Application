package remocra.db

import com.google.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectDistinct
import remocra.data.GlobalData
import remocra.db.jooq.couverturehydraulique.enums.EtudeStatut
import remocra.db.jooq.couverturehydraulique.tables.references.ETUDE
import remocra.db.jooq.couverturehydraulique.tables.references.L_ETUDE_COMMUNE
import remocra.db.jooq.couverturehydraulique.tables.references.TYPE_ETUDE
import remocra.db.jooq.remocra.tables.references.COMMUNE
import java.time.ZonedDateTime
import java.util.UUID

class CouvertureHydrauliqueRepository @Inject constructor(private val dsl: DSLContext) {

    fun getEtudes(limit: Int?, offset: Int?, filterBy: Filter?, sortBy: Sort?): Collection<EtudeComplete> =
        dsl.select(
            TYPE_ETUDE.LIBELLE,
            TYPE_ETUDE.ID,
            ETUDE.NUMERO,
            ETUDE.LIBELLE,
            ETUDE.DESCRIPTION,
            ETUDE.STATUT,
            multiset(
                selectDistinct(COMMUNE.ID, COMMUNE.CODE_INSEE, COMMUNE.LIBELLE)
                    .from(COMMUNE)
                    .join(L_ETUDE_COMMUNE)
                    .on(L_ETUDE_COMMUNE.COMMUNE_ID.eq(COMMUNE.ID))
                    .where(L_ETUDE_COMMUNE.ETUDE_ID.eq(ETUDE.ID)),
            ).convertFrom { record ->
                record?.map { r ->
                    CommuneSansGeometrie(
                        r.value1().let { it as UUID },
                        r.value2().toString(),
                        r.value3().toString(),
                    )
                }
            }.`as`("listeCommune"),
            ETUDE.DATE_MAJ,
        )
            .from(ETUDE)
            .join(TYPE_ETUDE)
            .on(ETUDE.TYPE_ETUDE_ID.eq(TYPE_ETUDE.ID))
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .orderBy(sortBy?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(ETUDE.LIBELLE))
            .limit(limit)
            .offset(offset)
            .fetchInto()

    fun getCountEtudes(filterBy: Filter?): Int =
        dsl.selectCount()
            .from(ETUDE)
            .where(filterBy?.toCondition() ?: DSL.trueCondition())
            .fetchSingleInto()

    data class EtudeComplete(
        val typeEtudeLibelle: String,
        val typeEtudeId: UUID,
        val etudeNumero: String,
        val etudeLibelle: String,
        val etudeDescription: String?,
        val listeCommune: Collection<CommuneSansGeometrie>,
        val etudeStatut: EtudeStatut,
        val etudeDateMaj: ZonedDateTime?,
    )

    data class CommuneSansGeometrie(
        val communeId: UUID,
        val communeCodeInsee: String,
        val communeLibelle: String,
    )

    data class Filter(
        val typeEtudeId: UUID?,
        val etudeNumero: String?,
        val etudeLibelle: String?,
        val etudeDescription: String?,
        val etudeStatut: EtudeStatut?,
    ) {
        fun toCondition(): Condition =
            DSL.and(
                listOfNotNull(
                    typeEtudeId?.let { DSL.and(ETUDE.TYPE_ETUDE_ID.eq(it)) },
                    etudeNumero?.let { DSL.and(ETUDE.NUMERO.contains(it)) },
                    etudeLibelle?.let { DSL.and(ETUDE.LIBELLE.contains(it)) },
                    etudeDescription?.let { DSL.and(ETUDE.DESCRIPTION.contains(it)) },
                    etudeStatut?.let { DSL.and(ETUDE.STATUT.eq(it)) },
                ),
            )
    }

    data class Sort(
        val typeEtudeLibelle: Int?,
        val etudeNumero: Int?,
        val etudeLibelle: Int?,
        val etudeDescription: Int?,
        val etudeStatut: Int?,
        val etudeDateMaj: Int?,
    ) {

        fun toCondition(): List<SortField<*>> = listOfNotNull(
            TYPE_ETUDE.LIBELLE.getSortField(typeEtudeLibelle),
            ETUDE.NUMERO.getSortField(etudeNumero),
            ETUDE.LIBELLE.getSortField(etudeLibelle),
            ETUDE.DESCRIPTION.getSortField(etudeDescription),
            ETUDE.STATUT.getSortField(etudeStatut),
            ETUDE.DATE_MAJ.getSortField(etudeDateMaj),
        )
    }

    fun getTypeEtudes(): Collection<GlobalData.IdCodeLibelleData> =
        dsl.select(
            TYPE_ETUDE.ID.`as`("id"),
            TYPE_ETUDE.CODE.`as`("code"),
            TYPE_ETUDE.LIBELLE.`as`("libelle"),
        ).from(TYPE_ETUDE).fetchInto()
}
