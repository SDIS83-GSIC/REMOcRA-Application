package remocra.usecase.rcci

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.app.ParametresProvider
import remocra.data.GlobalData
import remocra.data.enums.ParametreEnum
import remocra.db.UtilisateurRepository
import remocra.db.VoieRepository
import remocra.usecase.AbstractUseCase
import remocra.usecase.commune.GetCommuneVoieUseCase
import remocra.utils.getListOfString
import kotlin.text.get

class GetReferentielRcciUseCase @Inject constructor(
    private val utilisateurRepository: UtilisateurRepository,
    private val getCommuneVoieUseCase: GetCommuneVoieUseCase,
    private val parametresProvider: ParametresProvider,
    private val objectMapper: ObjectMapper,
) : AbstractUseCase() {

    /**
     * Récupère le référentiel RCCI et les communes/voies associées à une géométrie donnée.
     */
    fun get(geometrie: Geometry?): ReferentielRcci {
        val ddtmonf = utilisateurRepository.getUtilisateurDdtmonf(
            parametresProvider.get().mapParametres
                .getListOfString(ParametreEnum.LISTE_TYPE_ORGA_DDTM_ONF.name, objectMapper) ?: listOf(),
        )
        val sdis = utilisateurRepository.getUtilisateurSdis(
            parametresProvider.get().mapParametres
                .getListOfString(ParametreEnum.LISTE_TYPE_ORGA_SDIS.name, objectMapper) ?: listOf(),
        )
        val gendarmerie = utilisateurRepository.getUtilisateurGendarmerie(
            parametresProvider.get().mapParametres
                .getListOfString(ParametreEnum.LISTE_TYPE_ORGA_GENDARMERIE.name, objectMapper) ?: listOf(),
        )
        val police = utilisateurRepository.getUtilisateurPolice(
            parametresProvider.get().mapParametres
                .getListOfString(ParametreEnum.LISTE_TYPE_ORGA_POLICE.name, objectMapper) ?: listOf(),
        )

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
        val ddtmonf: Collection<GlobalData.IdLibelleData>,
        val sdis: Collection<GlobalData.IdLibelleData>,
        val gendarmerie: Collection<GlobalData.IdLibelleData>,
        val police: Collection<GlobalData.IdLibelleData>,
        val listCommune: Collection<GlobalData.IdCodeLibelleData>,
        val listVoie: Collection<VoieRepository.VoieWithCommune>,
    )
}
