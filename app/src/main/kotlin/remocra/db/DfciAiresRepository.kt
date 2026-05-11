package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.DfciAire
import remocra.db.jooq.remocra.tables.references.DFCI_AIRE
import java.util.UUID

/**
 * Classe repository permettant de récupérer des données dans la BD en lien avec les aires du module DFCI
 */
class DfciAiresRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Récupère l'aire dans la bd correspondant à l'id donné
     */
    fun getDfciAireByID(dfciAireId: UUID): DfciAire =
        dsl
            .selectFrom(DFCI_AIRE)
            .where(DFCI_AIRE.ID.eq(dfciAireId))
            .fetchSingleInto()

    fun updateDfciAire(data: DfciAire) =
        dsl
            .update(DFCI_AIRE)
            .set(DFCI_AIRE.AMENAGEMENT, data.dfciAireAmenagement)
            .set(DFCI_AIRE.DATE_GPS, data.dfciAireDateGps)
            .set(DFCI_AIRE.GRANDE_DIMENSION, data.dfciAireGrandeDimension)
            .set(DFCI_AIRE.PETITE_DIMENSION, data.dfciAirePetiteDimension)
            .set(DFCI_AIRE.TYPE, data.dfciAireType)
            .set(DFCI_AIRE.DFCI_PISTE_ID, data.dfciAireDfciPisteId)
            .set(DFCI_AIRE.REMARQUE, data.dfciAireRemarque)
            .set(DFCI_AIRE.VERSION, data.dfciAireVersion)
            .where(DFCI_AIRE.ID.eq(data.dfciAireId))
            .execute()
}
