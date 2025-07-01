package remocra.usecase.rcci

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.data.GlobalData
import remocra.db.UtilisateurRepository
import remocra.db.VoieRepository
import remocra.usecase.AbstractUseCase
import remocra.utils.GetCommuneVoieUseCase

class GetReferentielRcciUseCase @Inject constructor(
    private val utilisateurRepository: UtilisateurRepository,
    private val getCommuneVoieUseCase: GetCommuneVoieUseCase,
) : AbstractUseCase() {

    /**
     * Récupère le référentiel RCCI et les communes/voies associées à une géométrie donnée.
     */
    fun get(geometrie: Geometry?): ReferentielRcci {
        val ddtmonf = utilisateurRepository.getUtilisateurDdtmonf()
        val sdis = utilisateurRepository.getUtilisateurSdis()
        val gendarmerie = utilisateurRepository.getUtilisateurGendarmerie()
        val police = utilisateurRepository.getUtilisateurPolice()

        val communesVoies = getCommuneVoieUseCase.execute(geometrie)

        return ReferentielRcci(
            ddtmonf = ddtmonf,
            sdis = sdis,
            gendarmerie = gendarmerie,
            police = police,
            listCommune = communesVoies.listCommunes,
            listVoie = communesVoies.listVoies,
        )
    }

    data class ReferentielRcci(
        val ddtmonf: Collection<GlobalData.IdCodeLibelleData>,
        val sdis: Collection<GlobalData.IdCodeLibelleData>,
        val gendarmerie: Collection<GlobalData.IdCodeLibelleData>,
        val police: Collection<GlobalData.IdCodeLibelleData>,
        val listCommune: Collection<GlobalData.IdCodeLibelleData>,
        val listVoie: Collection<VoieRepository.VoieWithCommune>,
    )
}
