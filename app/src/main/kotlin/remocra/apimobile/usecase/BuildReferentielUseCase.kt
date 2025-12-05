package remocra.apimobile.usecase

import jakarta.inject.Inject
import remocra.apimobile.data.ContactForApiMobileData
import remocra.apimobile.data.ContactRoleForApiMobileData
import remocra.apimobile.data.PeiAnomalieForApiMobileData
import remocra.apimobile.data.PeiForApiMobileData
import remocra.apimobile.repository.ReferentielRepository
import remocra.app.DataCacheProvider
import remocra.app.ParametresProvider
import remocra.auth.WrappedUserInfo
import remocra.data.enums.ParametreEnum
import remocra.db.FonctionContactRepository
import remocra.db.GestionnaireRepository
import remocra.db.RoleRepository
import remocra.db.jooq.remocra.enums.Disponibilite
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.TypePei
import remocra.db.jooq.remocra.enums.TypeVisite
import remocra.db.jooq.remocra.tables.pojos.Anomalie
import remocra.db.jooq.remocra.tables.pojos.AnomalieCategorie
import remocra.db.jooq.remocra.tables.pojos.Domaine
import remocra.db.jooq.remocra.tables.pojos.FonctionContact
import remocra.db.jooq.remocra.tables.pojos.Gestionnaire
import remocra.db.jooq.remocra.tables.pojos.Nature
import remocra.db.jooq.remocra.tables.pojos.NatureDeci
import remocra.db.jooq.remocra.tables.pojos.Parametre
import remocra.db.jooq.remocra.tables.pojos.PoidsAnomalie
import remocra.db.jooq.remocra.tables.pojos.RoleContact
import remocra.usecase.AbstractUseCase
import remocra.utils.DisponibiliteDecorator
import remocra.utils.getLibelleTypeVisite
import java.util.UUID

class BuildReferentielUseCase : AbstractUseCase() {

    @Inject
    lateinit var dataCacheProvider: DataCacheProvider

    @Inject
    lateinit var referentielRepository: ReferentielRepository

    @Inject
    lateinit var gestionnaireRepository: GestionnaireRepository

    @Inject
    lateinit var fonctionContactRepository: FonctionContactRepository

    @Inject
    lateinit var roleRepository: RoleRepository

    @Inject
    lateinit var parametresProvider: ParametresProvider

    @Inject
    lateinit var decorateListPeiForApi: DecorateListPeiForApi

    @Inject
    lateinit var peiCaracteristiquesUseCase: PeiCaracteristiquesUseCase

    @Inject
    lateinit var dispoDecorator: DisponibiliteDecorator

    fun execute(userInfo: WrappedUserInfo): ReferentielResponse {
        val nomPrenom = userInfo.nom + " " + userInfo.prenom

        // On va chercher tous les paramètres rattachés à la section "MOBILE"
        val paramsMobile = ParametreEnum.entries.filter { it.section == ParametreEnum.ParametreSection.MOBILE }

        // pour toutes les clés trouvées, on remonte la valeur correspondante en base
        val parametresMobile = parametresProvider.get()
            .mapParametres.values.filter { paramsMobile.contains(ParametreEnum.valueOf(it.parametreCode)) }

        val setTypeVisiteAutorisees: MutableSet<TypeVisite> = mutableSetOf()
        if (userInfo.hasDroit(droitWeb = Droit.VISITE_RECEP_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.RECEPTION)
        }
        if (userInfo.hasDroit(droitWeb = Droit.VISITE_RECO_INIT_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.RECO_INIT)
        }
        if (userInfo.hasDroit(droitWeb = Droit.VISITE_CONTROLE_TECHNIQUE_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.CTP)
        }
        if (userInfo.hasDroit(droitWeb = Droit.VISITE_RECO_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.ROP)
        }
        if (userInfo.hasDroit(droitWeb = Droit.VISITE_NON_PROGRAMME_C)) {
            setTypeVisiteAutorisees.add(TypeVisite.NP)
        }

        return ReferentielResponse(
            listPei = decorateListPeiForApi.execute(referentielRepository.getPeiList()),
            listPeiAnomalies = referentielRepository.getPeiAnomalieList(),
            listGestionnaire = gestionnaireRepository.getAllForMobile(),
            listContact = referentielRepository.getContactList(),
            listRole = roleRepository.getAllForMobile(),
            listContactRole = referentielRepository.getContactRoleList(),
            listTypePei = TypePei.entries,
            listNature = dataCacheProvider.getNatures().values,
            listNatureDeci = dataCacheProvider.getNaturesDeci().values,
            listAnomalie = dataCacheProvider.getAnomalies().values,
            listAnomalieCategorie = dataCacheProvider.getAnomaliesCategories().values,
            listPoidsAnomalie = referentielRepository.getAnomaliePoidsList()
                .filter { it.poidsAnomalieTypeVisite?.intersect(setTypeVisiteAutorisees)?.isNotEmpty() ?: false }
                .map { poidsAnomalie ->
                    poidsAnomalie.copy(
                        poidsAnomalieTypeVisite = poidsAnomalie.poidsAnomalieTypeVisite?.intersect(setTypeVisiteAutorisees)?.toTypedArray(),
                    )
                },
            listTypeVisite = setTypeVisiteAutorisees.map {
                CodeLibelleTypeVisite(
                    codeTypeVisite = it,
                    libelleTypeVisite = getLibelleTypeVisite(it),
                )
            },
            listParametre = parametresMobile,
            listDroit = userInfo.droits!!.map { it.name },
            utilisateurConnecte = nomPrenom,
            peiCaracteristiques = peiCaracteristiquesUseCase.getPeiCaracteristiquesMobile(),
            listFonctionContact = fonctionContactRepository.getAllForMobile(),
            listDomaine = dataCacheProvider.getDomaines().values,
            mapDisponibiliteByLibelle = Disponibilite.entries.associateWith { dispoDecorator.decorateDisponibilite(it) },
        )
    }

    data class ReferentielResponse(
        val listPei: Collection<PeiForApiMobileData>,
        val listPeiAnomalies: Collection<PeiAnomalieForApiMobileData>,
        val listGestionnaire: Collection<Gestionnaire>,
        val listContact: Collection<ContactForApiMobileData>,
        val listRole: Collection<RoleContact>,
        val listContactRole: Collection<ContactRoleForApiMobileData>,
        val listTypePei: Collection<TypePei>,
        val listNature: Collection<Nature>,
        val listNatureDeci: Collection<NatureDeci>,
        val listAnomalie: Collection<Anomalie>,
        val listAnomalieCategorie: Collection<AnomalieCategorie>,
        val listPoidsAnomalie: Collection<PoidsAnomalie>,
        val listTypeVisite: Collection<CodeLibelleTypeVisite>,
        val listParametre: Collection<Parametre>,
        val listDroit: Collection<String>,
        val listFonctionContact: Collection<FonctionContact>,
        val listDomaine: Collection<Domaine>,

        val utilisateurConnecte: String,
        val peiCaracteristiques: Map<UUID, String>,
        val mapDisponibiliteByLibelle: Map<Disponibilite, String>,
    )

    data class CodeLibelleTypeVisite(
        val codeTypeVisite: TypeVisite,
        val libelleTypeVisite: String,
    )
}
