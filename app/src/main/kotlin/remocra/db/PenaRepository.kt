package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.InsertSetStep
import org.jooq.Record
import org.jooq.impl.DSL
import remocra.data.PenaData
import remocra.db.PeiRepository.Companion.peiData
import remocra.db.jooq.remocra.tables.Pei.Companion.PEI
import remocra.db.jooq.remocra.tables.pojos.LPenaTypeEngin
import remocra.db.jooq.remocra.tables.pojos.Pena
import remocra.db.jooq.remocra.tables.references.L_PENA_TYPE_ENGIN
import remocra.db.jooq.remocra.tables.references.PENA
import java.util.UUID

class PenaRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    fun getInfoPena(penaId: UUID): PenaData =
        dsl.select(peiData).select(
            PENA.CAPACITE,
            PENA.DISPONIBILITE_HBE,
            PENA.QUANTITE_APPOINT,
            PENA.CAPACITE_ILLIMITEE,
            PENA.CAPACITE_INCERTAINE,
            PENA.MATERIAU_ID,
            PENA.EQUIPE_HBE,
        ).select(
            DSL.multiset(dsl.select(L_PENA_TYPE_ENGIN.TYPE_ENGIN_ID).from(L_PENA_TYPE_ENGIN).where(L_PENA_TYPE_ENGIN.PENA_ID.eq(penaId)))
                .convertFrom { record ->
                    record?.map { r ->
                        r.value1().let { it as UUID }
                    }
                }.`as`("typeEnginIds"),
        )
            .from(PEI)
            .join(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .where(PEI.ID.eq(penaId))
            .fetchSingleInto()

    fun upsertPena(pena: PenaData): Int =
        dsl.insertInto(PENA)
            .setPenaField(pena)

    /**
     * Permet d'insérer ou d'update les champs d'un PENA.
     * Le jour où un champ est ajouté, il suffira de mettre à jour cette fonction.
     */
    private fun <R : Record?> InsertSetStep<R>.setPenaField(pena: PenaData): Int {
        val record = dsl.newRecord(
            PENA,
            Pena(
                penaId = pena.peiId,
                penaCapacite = pena.penaCapacite,
                penaMateriauId = pena.penaMateriauId,
                penaCapaciteIllimitee = pena.penaCapaciteIllimitee,
                penaQuantiteAppoint = pena.penaQuantiteAppoint,
                penaDisponibiliteHbe = pena.penaDisponibiliteHbe,
                penaCapaciteIncertaine = pena.penaCapaciteIncertaine,
                penaCoordonneDfci = null, // TODO ?
                penaEquipeHbe = pena.penaEquipeHbe,
            ),
        )

        return set(record).onConflict(PENA.ID)
            .doUpdate()
            .set(record)
            .execute()
    }

    fun deleteById(peiId: UUID) = dsl.deleteFrom(PENA).where(PENA.ID.eq(peiId)).execute()

    fun deleteLienPenaTypeEngin(
        penaId: UUID,
    ) =
        dsl.deleteFrom(L_PENA_TYPE_ENGIN)
            .where(L_PENA_TYPE_ENGIN.PENA_ID.eq(penaId))
            .execute()

    fun addLienPenaTypeEngin(
        penaId: UUID,
        typeEnginIds: Collection<UUID>,
    ) = dsl.batch(
        typeEnginIds.map {
            DSL
                .insertInto(L_PENA_TYPE_ENGIN)
                .set(dsl.newRecord(L_PENA_TYPE_ENGIN, LPenaTypeEngin(penaId = penaId, typeEnginId = it)))
        },
    ).execute()

    fun getListPenaData(): List<PenaData> =
        dsl.select(peiData).select(
            PENA.CAPACITE,
            PENA.DISPONIBILITE_HBE,
            PENA.QUANTITE_APPOINT,
            PENA.CAPACITE_ILLIMITEE,
            PENA.CAPACITE_INCERTAINE,
            PENA.MATERIAU_ID,
            PENA.EQUIPE_HBE,
        ).select(
            DSL.multiset(dsl.select(L_PENA_TYPE_ENGIN.TYPE_ENGIN_ID).from(L_PENA_TYPE_ENGIN).where(L_PENA_TYPE_ENGIN.PENA_ID.eq(PENA.ID)))
                .convertFrom { record ->
                    record?.map { r ->
                        r.value1().let { it as UUID }
                    }
                }.`as`("typeEnginIds"),
        )
            .from(PEI)
            .join(PENA)
            .on(PENA.ID.eq(PEI.ID))
            .fetchInto()
}
