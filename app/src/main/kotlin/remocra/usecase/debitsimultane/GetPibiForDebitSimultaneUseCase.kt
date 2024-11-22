package remocra.usecase.debitsimultane

import com.google.inject.Inject
import org.locationtech.jts.algorithm.Centroid
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.PrecisionModel
import remocra.CoordonneesXYSrid
import remocra.app.AppSettings
import remocra.data.GlobalData
import remocra.db.DebitSimultaneRepository
import remocra.db.PeiRepository
import remocra.usecase.AbstractUseCase
import java.util.UUID

class GetPibiForDebitSimultaneUseCase : AbstractUseCase() {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var peiRepository: PeiRepository

    @Inject
    private lateinit var appSettings: AppSettings

    fun execute(coordonneesXYSrid: CoordonneesXYSrid?, listePibiId: Set<UUID>?, typeReseauId: UUID): Collection<GlobalData.IdCodeLibelleData> {
        // Si on est dans le cas d'une création et qu'on a donc pas la géométrie du débit simultané
        var debitGeometrie = coordonneesXYSrid
        if (coordonneesXYSrid == null) {
            val listePoint = peiRepository.getGeometriesPei(listePibiId!!)

            val centroid = Centroid(
                MultiPoint(
                    listePoint.toTypedArray(),
                    GeometryFactory(
                        PrecisionModel(),
                        appSettings.srid,
                    ),
                ),
            ).centroid

            debitGeometrie = CoordonneesXYSrid(
                centroid.x,
                centroid.y,
                appSettings.srid,
            )
        }

        return debitSimultaneRepository.getPibiForDebitSimultane(
            debitGeometrie!!,
            typeReseauId,
        )
    }

    fun checkDistance(listePibiId: Set<UUID>): Boolean {
        val listePoint = peiRepository.getGeometriesPei(listePibiId)

        val centroid = Centroid(
            MultiPoint(
                listePoint.toTypedArray(),
                GeometryFactory(
                    PrecisionModel(),
                    appSettings.srid,
                ),
            ),
        ).centroid

        val listDistance = debitSimultaneRepository.getDistance(
            listePibiId,
            CoordonneesXYSrid(
                coordonneeX = centroid.x,
                coordonneeY = centroid.y,
                srid = appSettings.srid,
            ),
        )

        return listDistance.all { it }
    }

    fun getInfosTypeReseauMaxDiametre(listePibiId: Set<UUID>): DebitSimultaneRepository.TypeReseauMaxCanalisationSite {
        val infos = debitSimultaneRepository.getInfosGenerales(listePibiId)

        return DebitSimultaneRepository.TypeReseauMaxCanalisationSite(
            siteLibelle = infos.map { it.siteLibelle }.firstOrNull { it !== null },
            typeReseauLibelle = infos.map { it.typeReseauLibelle }.first(),
            pibiDiametreCanalisation = infos.map { it.pibiDiametreCanalisation }.sortedByDescending { it }.firstOrNull(),
        )
    }
}
