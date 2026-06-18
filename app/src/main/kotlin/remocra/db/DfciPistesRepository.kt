package remocra.db

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Field
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.GlobalData
import remocra.db.jooq.remocra.tables.references.DFCI_PISTE
import remocra.utils.ST_DWithin
import remocra.utils.ST_Transform

/**
 * Repository permettant de réaliser les différentes requêtes liées aux pistes à la bd
 */
class DfciPistesRepository @Inject constructor(
    private val dsl: DSLContext,
    private val appSettings: AppSettings,
) : AbstractRepository() {

    /**
     * Récupère la liste des id, code et libelle des pistes
     * @return la collection
     */
    fun getIdCodeLibelleDfciPiste(
        geometrie: Field<Geometry?>,
        tolerance: Int,
    ): Collection<GlobalData.IdCodeLibelleData> {
        val transfoGeom = ST_Transform(geometrie, appSettings.srid)
        return dsl
            .select(
                DFCI_PISTE.ID.`as`("id"),
                DFCI_PISTE.CODE.`as`("code"),
                DFCI_PISTE.LIBELLE.`as`("libelle"),
            )
            .from(DFCI_PISTE)
            .where(
                ST_DWithin(
                    DFCI_PISTE.GEOMETRIE,
                    transfoGeom,
                    tolerance.toDouble(),
                ),
            )
            .orderBy(DFCI_PISTE.LIBELLE)
            .fetchInto()
    }
}
