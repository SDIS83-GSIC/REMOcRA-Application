package remocra.db

import com.google.inject.Inject
import org.jooq.CommonTableExpression
import org.jooq.DSLContext
import org.jooq.Record1
import org.jooq.impl.DSL
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.select
import remocra.data.enums.CodeSdis
import remocra.db.jooq.couverturehydraulique.tables.references.COUVERTURE_TRACEE
import remocra.db.jooq.couverturehydraulique.tables.references.COUVERTURE_TRACEE_PEI
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.couverturehydraulique.tables.references.RESEAU
import remocra.db.jooq.remocra.tables.references.PEI
import java.util.UUID

class CouvertureHydrauliqueCalculRepository @Inject constructor(
    private val dsl: DSLContext,
) {

    fun executeInsererJoinctionPei(
        distanceMaxAuReseau: Int,
        etudeId: UUID?,
        listPeiId: Set<UUID>,
        listPeiProjetId: Set<UUID>,
        useReseauImporteWithCourant: Boolean,
    ) {
        val peiWithPeiProjetCte = getTablePei(listPeiId, listPeiProjetId)

        dsl.with(peiWithPeiProjetCte).select(
            DSL.field(
                """
                    couverturehydraulique.inserer_jonction_pei(
                        ${peiWithPeiProjetCte.field("PEI_ID")}, 
                        $distanceMaxAuReseau,
                        ${etudeId.let {
                    if (it == null) {
                        "$it,"
                    } else {
                        "'$it'::uuid, "
                    }
                }}
                        $useReseauImporteWithCourant)
                        """,
            ),
        )
            .from(peiWithPeiProjetCte)
            .execute()
    }

    fun executeParcoursCouverture(
        reseauImporte: UUID?,
        etudeId: UUID,
        distances: List<Int>,
        listPeiId: Set<UUID>,
        listPeiProjetId: Set<UUID>,
        profondeurCouverture: Int,
        useReseauImporteWithCourant: Boolean,
    ) {
        val peiWithPeiProjetCte = getTablePei(listPeiId, listPeiProjetId)
        dsl.with(peiWithPeiProjetCte)
            .select(
                DSL.field(
                    """
                        couverturehydraulique.parcours_couverturehydraulique(
                            ${peiWithPeiProjetCte.field("PEI_ID")}, 
                            '$etudeId'::uuid, 
                            ${reseauImporte.let {
                        if (it == null) {
                            "$it,"
                        } else {
                            "'$it'::uuid,"
                        }
                    }} array$distances,
                            $profondeurCouverture,
                            $useReseauImporteWithCourant
                        )
                        """,
                ),
            )
            .from(peiWithPeiProjetCte)
            .execute()
    }

    fun executeCouvertureHydrauliqueZonage(
        etudeId: UUID,
        distances: List<Int>,
        listPeiId: Set<UUID>,
        listPeiProjetId: Set<UUID>,
        profondeurCouverture: Int,
        srid: Int,
        codeSdis: CodeSdis,
    ) {
        val peiWithPeiProjetCte = getTablePei(listPeiId, listPeiProjetId)
        dsl.with(peiWithPeiProjetCte)
            .select(
                DSL.field(
                    (
                        """
                        couverturehydraulique.couverturehydraulique_zonage(
                            '$etudeId'::uuid, array$distances, $profondeurCouverture,
                            $srid, '${codeSdis.name}')
                            """
                        ),
                ),
            )
            .from(peiWithPeiProjetCte)
            .execute()
    }

    fun executeRetirerJonctionPei() {
        dsl
            .select(
                DSL.field("couverturehydraulique.retirer_jonction_pei(${RESEAU.PEI_TRONCON})"),
            )
            .from(RESEAU)
            .where(RESEAU.PEI_TRONCON.isNotNull())
            .execute()
    }

    fun deleteCouverture(idEtude: UUID) {
        dsl
            .deleteFrom(COUVERTURE_TRACEE_PEI)
            .where(COUVERTURE_TRACEE_PEI.ETUDE_ID.eq(idEtude))
            .execute()

        dsl
            .deleteFrom(COUVERTURE_TRACEE)
            .where(COUVERTURE_TRACEE.ETUDE_ID.eq(idEtude))
            .execute()
    }

    private fun getTablePei(
        listPeiId: Set<UUID>,
        listPeiProjetId: Set<UUID>,
    ): CommonTableExpression<Record1<UUID?>> {
        val peiWithPeiProjetCteName = name("PEI_WITH_PEI_PROJET")
        return peiWithPeiProjetCteName.fields("PEI_ID").`as`(
            select(
                PEI.ID,
            )
                .from(PEI)
                .where(PEI.ID.`in`(listPeiId))
                .union(
                    select(
                        PEI_PROJET.ID.`as`("PEI_ID"),
                    )
                        .from(PEI_PROJET)
                        .where(PEI_PROJET.ID.`in`(listPeiProjetId)),
                ),
        )
    }
}
