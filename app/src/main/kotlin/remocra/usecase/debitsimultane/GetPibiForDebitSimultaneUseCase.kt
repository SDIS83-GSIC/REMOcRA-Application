package remocra.usecase.debitsimultane

import com.google.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.AppSettings
import remocra.data.GlobalData
import remocra.db.DebitSimultaneRepository
import remocra.db.PeiRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.calculerCentroide
import java.util.UUID

class GetPibiForDebitSimultaneUseCase : AbstractUseCase() {

    @Inject
    private lateinit var debitSimultaneRepository: DebitSimultaneRepository

    @Inject
    private lateinit var peiRepository: PeiRepository

    @Inject
    private lateinit var appSettings: AppSettings

    fun execute(geometry: Geometry?, listePibiId: Set<UUID>?, typeReseauId: UUID): Collection<GlobalData.IdCodeLibelleData> {
        // Si on est dans le cas d'une création et qu'on a donc pas la géométrie du débit simultané
        var debitGeometrie = geometry
        if (geometry == null) {
            debitGeometrie = calculerCentroide(peiRepository.getGeometriesPei(listePibiId!!))
        }

        return debitSimultaneRepository.getPibiForDebitSimultane(
            debitGeometrie!!,
            typeReseauId,
        )
    }

    fun checkDistance(listePibiId: Set<UUID>): Boolean {
        val listDistance = debitSimultaneRepository.getDistance(
            listePibiId,
            calculerCentroide(peiRepository.getGeometriesPei(listePibiId)) ?: return false,
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
