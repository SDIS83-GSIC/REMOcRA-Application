package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.data.PeiAnomalieForApiMobileData
import remocra.apimobile.data.PeiForApiMobileData
import remocra.apimobile.repository.ReferentielRepository
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.auth.UserInfo
import remocra.data.GlobalData
import remocra.data.enums.ParametreEnum
import remocra.db.GestionnaireRepository
import remocra.db.RoleRepository
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.usecase.AbstractUseCase
import java.util.UUID

class BuildReferentielUseCase : AbstractUseCase() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var referentielRepository: ReferentielRepository

    @Inject
    lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    lateinit var roleRepository: RoleRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var buildAdresseCompleteUseCase: BuildAdresseCompleteUseCase

    @Inject
    lateinit var peiCaracteristiquesUseCase: PeiCaracteristiquesUseCase

    fun execute(userInfo: UserInfo): ReferentielResponse {
        val nomPrenom = userInfo.utilisateur.utilisateurNom + " " + userInfo.utilisateur.utilisateurPrenom

        // On va chercher tous les paramètres rattachés à la section "MOBILE"
        val paramsMobile = ParametreEnum.entries.filter { it.section == ParametreEnum.ParametreSection.MOBILE }

        // pour toutes les clés trouvées, on remonte la valeur correspondante en base
        val parametresMobile = parametresProvider.get()
            .mapParametres.values.filter { paramsMobile.contains(ParametreEnum.valueOf(it.parametreCode)) }

        val setTypeVisiteAutorisees: MutableSet<TypeVisite> = mutableSetOf()
        if (userInfo.droits.contains(Droit.VISITE_RECEP_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.RECEPTION)
        }
        if (userInfo.droits.contains(Droit.VISITE_RECO_INIT_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.RECO_INIT)
        }
        if (userInfo.droits.contains(Droit.VISITE_CONTROLE_TECHNIQUE_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.CTP)
        }
        if (userInfo.droits.contains(Droit.VISITE_RECO_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.RECOP)
        }
        if (userInfo.droits.contains(Droit.VISITE_NON_PROGRAMME_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.NP)
        }

        return ReferentielResponse(
            listPei = buildAdresseCompleteUseCase.execute(referentielRepository.getPeiList()),
            listPeiAnomalies = referentielRepository.getPeiAnomalieList(),
            listGestionnaire = gestionnaireRepository.getAll(),
            listContact = referentielRepository.getContactList(),
            listRole = roleRepository.getAll(),
            listContactRole = referentielRepository.getContactRoleList(),
            listTypePei = TypePei.entries,
            listNature = dataCacheProvider.getNatures().values,
            listNatureDeci = dataCacheProvider.getNaturesDeci().values,
            listAnomalie = dataCacheProvider.getAnomalies().values,
            listAnomalieCategorie = dataCacheProvider.getAnomaliesCategories().values,
            listPoidsAnomalie = referentielRepository.getAnomaliePoidsList().filter { it.poidsAnomalieTypeVisite?.intersect(setTypeVisiteAutorisees)?.isNotEmpty() ?: false },
            listTypeVisite = TypeVisite.entries,
            listParametre = parametresMobile,
            listDroit = userInfo.droits.map { it.name },
            utilisateurConnecte = nomPrenom,
            peiCaracteristiques = peiCaracteristiquesUseCase.getPeiCaracteristiques(),
        )
    }

    data class ReferentielResponse(
        val listPei: Collection<PeiForApiMobileData>,
        val listPeiAnomalies: Collection<PeiAnomalieForApiMobileData>,
        val listGestionnaire: Collection<GlobalData.IdCodeLibelleData>,
        val listContact: Collection<ContactForApiMobileData>,
        val listRole: Collection<GlobalData.IdCodeLibelleData>,
        val listContactRole: Collection<ContactRoleForApiMobileData>,
        val listTypePei: Collection<TypePei>,
        val listNature: Collection<Nature>,
        val listNatureDeci: Collection<NatureDeci>,
        val listAnomalie: Collection<Anomalie>,
        val listAnomalieCategorie: Collection<AnomalieCategorie>,
        val listPoidsAnomalie: Collection<PoidsAnomalie>,
        val listTypeVisite: Collection<TypeVisite>,
        val listParametre: Collection<Parametre>,
        val listDroit: Collection<String>,
        // TODO ajouter les fonctions des contacts et sûrement d'autres choses

        val utilisateurConnecte: String,
        val peiCaracteristiques: Map<UUID, String>,
    )
}
