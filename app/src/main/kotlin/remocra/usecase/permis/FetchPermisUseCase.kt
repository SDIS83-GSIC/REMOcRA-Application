package remocra.usecase.permis

import jakarta.inject.Inject
import remocra.GlobalConstants
import remocra.app.ParametresProvider
import remocra.data.GlobalData
import remocra.data.enums.TypeDataCache
import remocra.db.CadastreRepository
import remocra.db.CommuneRepository
import remocra.db.PermisRepository
import remocra.db.VoieRepository
import remocra.usecase.AbstractUseCase
import remocra.usecase.nomenclature.NomenclatureUseCase

class FetchPermisUseCase : AbstractUseCase() {
    @Inject lateinit var communeRepository: CommuneRepository

    @Inject lateinit var voieRepository: VoieRepository

    @Inject lateinit var permisRepository: PermisRepository

    @Inject lateinit var cadastreRepository: CadastreRepository

    @Inject lateinit var nomenclatureUseCase: NomenclatureUseCase

    @Inject lateinit var parametresProvider: ParametresProvider

    // On décide de remonter les 25 parcelles les plus proches de notre point de déclaration de permis
    companion object {
        private const val NB_PARCELLES_A_REMONTER: Int = 25
    }

    fun fetchPermisData(coordonneeX: String, coordonneeY: String, srid: Int): PermisFormData {
        val toleranceVoie = parametresProvider.getParametreInt(GlobalConstants.TOLERANCE_VOIES_METRES)
            ?: throw IllegalArgumentException("Le paramètre TOLERANCE_VOIES_METRES est nul, veuillez renseigner une valeur")

        val communeData =
            communeRepository.getCommuneByCoords(coordonneeX = coordonneeX, coordonneeY = coordonneeY, srid = srid)
                ?: throw IllegalStateException("Aucune commune ne se trouve sur les coordonnées fournies")

        return PermisFormData(
            communeData = communeData,
            listeVoie = voieRepository.getVoies(coordonneeX, coordonneeY, srid, toleranceVoie, listOf(communeData.id)),
            listeAvis = permisRepository.getAvis(),
            listeInterservice = permisRepository.getInterservice(),
            listeServiceInstructeur = nomenclatureUseCase.getListIdLibelle(TypeDataCache.TYPE_ORGANISME),
            listeCadastreParcelle = cadastreRepository.getParcelleFromCoordsForCombo(coordonneeX, coordonneeY, srid, NB_PARCELLES_A_REMONTER),
        )
    }

    data class PermisFormData(
        val communeData: GlobalData.IdCodeLibellePprifData,
        val listeVoie: Collection<VoieRepository.VoieWithCommune>,
        val listeAvis: Collection<GlobalData.IdCodeLibellePprifData>,
        val listeInterservice: Collection<GlobalData.IdCodeLibellePprifData>,
        val listeServiceInstructeur: List<GlobalData.IdCodeLibelleLienData>,
        val listeCadastreParcelle: Collection<GlobalData.IdCodeLibelleData>,
    )
}
