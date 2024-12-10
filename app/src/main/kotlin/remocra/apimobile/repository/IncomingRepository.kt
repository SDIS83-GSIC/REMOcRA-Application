package remocra.apimobile.repository

import org.jooq.DSLContext
import org.locationtech.jts.geom.Geometry
import remocra.GlobalConstants
import remocra.db.AbstractRepository
import remocra.db.fetchOneInto
import remocra.db.jooq.incoming.tables.references.GESTIONNAIRE
import remocra.db.jooq.incoming.tables.references.NEW_PEI
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.tables.references.COMMUNE
import remocra.db.jooq.remocra.tables.references.VOIE
import remocra.utils.ST_SetSrid
import remocra.utils.ST_Transform
import remocra.utils.ST_Within
import remocra.utils.toGeomFromText
import java.util.UUID
import javax.inject.Inject

class IncomingRepository @Inject constructor(
    private val dsl: DSLContext,
) : AbstractRepository() {

    /** Crée un PEI  */
    fun insertPei(
        peiId: UUID,
        gestionnaireId: UUID?,
        communeId: UUID,
        natureId: UUID,
        natureDeciId: UUID,
        peiTypePei: TypePei,
        peiObservation: String?,
        peiGeometrie: Geometry,
        peiVoieId: UUID?,
        srid: Int,
    ): Int =
        dsl
            .insertInto(NEW_PEI)
            .set(NEW_PEI.ID, peiId)
            .set(
                NEW_PEI.GEOMETRIE,
                ST_Transform(
                    ST_SetSrid(
                        peiGeometrie.toGeomFromText(),
                        GlobalConstants.SRID_4326,
                    ),
                    srid,
                ),

            )
            .set(NEW_PEI.TYPE_PEI, peiTypePei)
            .set(NEW_PEI.COMMUNE_ID, communeId)
            .set(NEW_PEI.VOIE_ID, peiVoieId)
            .set(NEW_PEI.NATURE_ID, natureDeciId)
            .set(NEW_PEI.NATURE_ID, natureId)
            .set(NEW_PEI.GESTIONNAIRE_ID, gestionnaireId)
            .set(NEW_PEI.OBSERVATION, peiObservation)
            .onConflictDoNothing()
            .execute()

    /**
     * Permet de récupérer l'id de la commune en fonction de la géométrie du PEI
     *
     * @param geometrie
     * @return
     */
    fun getCommuneWithGeometrie(geometrie: Geometry, srid: Int): UUID? =
        dsl
            .select(COMMUNE.ID)
            .from(COMMUNE)
            .where(
                ST_Within(
                    ST_Transform(
                        ST_SetSrid(
                            geometrie.toGeomFromText(),
                            srid = GlobalConstants.SRID_4326,
                        ),
                        srid = srid,
                    ),
                    COMMUNE.GEOMETRIE,
                ),
            )
            .fetchOneInto()

    fun getVoie(geometrie: Geometry, srid: Int): UUID? =
        dsl
            .select(VOIE.LIBELLE)
            .from(VOIE)
            .where(
                ST_Within(
                    ST_Transform(
                        ST_SetSrid(
                            geometrie.toGeomFromText(),
                            srid = GlobalConstants.SRID_4326,
                        ),
                        srid = srid,
                    ),
                    VOIE.GEOMETRIE,
                ),
            )
            .fetchOneInto()

    fun insertGestionnaireIncoming(
        gestionnaireCode: String?,
        gestionnaireLibelle: String?,
        gestionnaireId: UUID,
    ) =
        dsl
            .insertInto(GESTIONNAIRE)
            .set(GESTIONNAIRE.ID, gestionnaireId)
            .set(GESTIONNAIRE.CODE, gestionnaireCode)
            .set(GESTIONNAIRE.LIBELLE, gestionnaireLibelle)
            .onConflictDoNothing()
            .execute()
}
