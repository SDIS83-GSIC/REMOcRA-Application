import MapPei from "./components/Map/MapPei/MapPei.tsx";
import ModuleAdmin from "./components/ModuleRemocra/ModuleAdmin.tsx";
import ModuleCouvertureHydraulique from "./components/ModuleRemocra/ModuleCouvertureHydraulique.tsx";
import ModuleDeci from "./components/ModuleRemocra/ModuleDeci.tsx";
import NoMatch from "./components/Router/NoMatch.tsx";
import { Authorization } from "./droits.tsx";
import { TYPE_DROIT } from "./Entities/UtilisateurEntity.tsx";
import url from "./module/fetch.tsx";
import Accueil from "./pages/Accueil/Accueil.tsx";
import CreateDiametre from "./pages/Admin/diametre/CreateDiametre.tsx";
import ListDiametre from "./pages/Admin/diametre/ListDiametre.tsx";
import UpdateDiametre from "./pages/Admin/diametre/UpdateDiametre.tsx";
import CreateDomaine from "./pages/Admin/domaine/CreateDomaine.tsx";
import ListDomaine from "./pages/Admin/domaine/ListDomaine.tsx";
import UpdateDomaine from "./pages/Admin/domaine/UpdateDomaine.tsx";
import CreateContact from "./pages/Admin/contact/CreateContact.tsx";
import CreateGestionnaire from "./pages/Admin/Gestionnaire/CreateGestionnaire.tsx";
import ListContact from "./pages/Admin/contact/ListContact.tsx";
import ListGestionnaire from "./pages/Admin/Gestionnaire/ListGestionnaire.tsx";
import UpdateContact from "./pages/Admin/contact/UpdateContact.tsx";
import UpdateGestionnaire from "./pages/Admin/Gestionnaire/UpdateGestionnaire.tsx";
import CreateMarquePibi from "./pages/Admin/marquePibi/CreateMarquePibi.tsx";
import ListMarquePibi from "./pages/Admin/marquePibi/ListMarquePibi.tsx";
import UpdateMarquePibi from "./pages/Admin/marquePibi/UpdateMarquePibi.tsx";
import CreateMateriau from "./pages/Admin/materiau/CreateMateriau.tsx";
import ListMateriau from "./pages/Admin/materiau/ListMateriau.tsx";
import UpdateMateriau from "./pages/Admin/materiau/UpdateMateriau.tsx";
import MenuAdmin from "./pages/Admin/MenuAdmin.tsx";
import CreateModelePibi from "./pages/Admin/modelePibi/CreateModelePibi.tsx";
import ListModelePibi from "./pages/Admin/modelePibi/ListModelePibi.tsx";
import UpdateModelePibi from "./pages/Admin/modelePibi/UpdateModelePibi.tsx";
import CreateNature from "./pages/Admin/nature/CreateNature.tsx";
import ListNature from "./pages/Admin/nature/ListNature.tsx";
import UpdateNature from "./pages/Admin/nature/UpdateNature.tsx";
import CreateNatureDeci from "./pages/Admin/natureDeci/CreateNatureDeci.tsx";
import ListNatureDeci from "./pages/Admin/natureDeci/ListNatureDeci.tsx";
import UpdateNatureDeci from "./pages/Admin/natureDeci/UpdateNatureDeci.tsx";
import CreateNiveau from "./pages/Admin/niveau/CreateNiveau.tsx";
import ListNiveau from "./pages/Admin/niveau/ListNiveau.tsx";
import UpdateNiveau from "./pages/Admin/niveau/UpdateNiveau.tsx";
import CreateOrganisme from "./pages/Admin/organisme/CreateOrganisme.tsx";
import ListOrganisme from "./pages/Admin/organisme/ListOrganisme.tsx";
import UpdateOrganisme from "./pages/Admin/organisme/UpdateOrganisme.tsx";
import CreateProfilOrganisme from "./pages/Admin/profilOrganisme/CreateProfilOrganismes.tsx";
import ListProfilOrganisme from "./pages/Admin/profilOrganisme/ListProfilOrganisme.tsx";
import UpdateProfilOrganisme from "./pages/Admin/profilOrganisme/UpdateProfilOrganisme.tsx";
import CreateProfilUtilisateur from "./pages/Admin/profilUtilisateur/CreateProfilUtilisateur.tsx";
import ListProfilUtilisateur from "./pages/Admin/profilUtilisateur/ListProfilUtilisateur.tsx";
import UpdateProfilUtilisateur from "./pages/Admin/profilUtilisateur/UpdateProfilUtilisateur.tsx";
import CreateRoleContact from "./pages/Admin/roleContact/CreateRoleContact.tsx";
import ListRoleContact from "./pages/Admin/roleContact/ListRoleContact.tsx";
import UpdateRoleContact from "./pages/Admin/roleContact/UpdateRolecontact.tsx";
import ListSite from "./pages/Admin/site/ListSite.tsx";
import UpdateSite from "./pages/Admin/site/UpdateSite.tsx";
import CreateTypeCanalisation from "./pages/Admin/typeCanalisation/CreateTypeCanalisation.tsx";
import ListTypeCanalisation from "./pages/Admin/typeCanalisation/ListTypeCanalisation.tsx";
import UpdateTypeCanalisation from "./pages/Admin/typeCanalisation/UpdateTypeCanalisation.tsx";
import CreateTypeEtude from "./pages/Admin/typeEtude/CreateTypeEtude.tsx";
import ListTypeEtude from "./pages/Admin/typeEtude/ListTypeEtude.tsx";
import UpdateTypeEtude from "./pages/Admin/typeEtude/UpdateTypeEtude.tsx";
import CreateTypeOrganisme from "./pages/Admin/TypeOrganisme/CreateTypeOrganismes.tsx";
import ListTypeOrganisme from "./pages/Admin/TypeOrganisme/ListTypeOrganisme.tsx";
import UpdateTypeOrganisme from "./pages/Admin/TypeOrganisme/UpdateTypeOrganisme.tsx";
import CreateTypePenaAspiration from "./pages/Admin/typePenaAspiration/CreateTypePenaAspiration.tsx";
import ListTypePenaAspiration from "./pages/Admin/typePenaAspiration/ListTypePenaAspiration.tsx";
import UpdateTypePenaAspiration from "./pages/Admin/typePenaAspiration/UpdateTypePenaAspiration.tsx";
import CreateTypeReseau from "./pages/Admin/typeReseau/CreateTypeReseau.tsx";
import ListTypeReseau from "./pages/Admin/typeReseau/ListTypeReseau.tsx";
import UpdateTypeReseau from "./pages/Admin/typeReseau/UpdateTypeReseau.tsx";
import GenereCourrier from "./pages/Courrier/GenereCourrier.tsx";
import ViewCourrier from "./pages/Courrier/ViewCourrier.tsx";
import CreateEtude from "./pages/CouvertureHydraulique/Etude/CreateEtude.tsx";
import ImportShapeEtude from "./pages/CouvertureHydraulique/Etude/ImportShapeEtude.tsx";
import ListEtude from "./pages/CouvertureHydraulique/Etude/ListEtude.tsx";
import MapEtude from "./pages/CouvertureHydraulique/Etude/MapEtude.tsx";
import UpdateEtude from "./pages/CouvertureHydraulique/Etude/UpdateEtude.tsx";
import UpdatePeiProjet from "./pages/CouvertureHydraulique/PeiProjet/UpdatePeiProjet.tsx";
import CreateIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/CreateIndisponibiliteTemporaire.tsx";
import ListIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/ListIndisponibiliteTemporaire.tsx";
import UpdateIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/UpdateIndisponibiliteTemporaire.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import CreatePei from "./pages/Pei/CreatePei.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";
import CreateTournee from "./pages/Tournee/CreateTournee.tsx";
import ListTournee from "./pages/Tournee/ListTournee.tsx";
import TourneePei from "./pages/Tournee/TourneePei.tsx";
import UpdateTournee from "./pages/Tournee/UpdateTournee.tsx";
import ValidateAccessSaisieVisiteTournee from "./pages/Visite/SaisieVisiteTournee.tsx";
import Visite from "./pages/Visite/Visite.tsx";
import ListThematique from "./pages/Admin/thematique/ListThematique.tsx";
import UpdateThematique from "./pages/Admin/thematique/UpdateThematique.tsx";
import CreateThematique from "./pages/Admin/thematique/CreateThematique.tsx";
import ListBlocDocument from "./pages/Admin/blocDocument/ListBlocDocument.tsx";
import CreateBlocDocument from "./pages/Admin/blocDocument/CreateBlocDocument.tsx";
import UpdateBlocDocument from "./pages/Admin/blocDocument/UpdateBlocDocument.tsx";
import AdminFicheResume from "./pages/Admin/resume/AdminFicheResume.tsx";
import ListUtilisateur from "./pages/Admin/utilisateur/ListUtilisateur.tsx";
import AnomalieList from "./pages/Admin/anomalie/AnomalieList.tsx";
import AnomalieCreate from "./pages/Admin/anomalie/AnomalieCreate.tsx";
import AnomalieUpdate from "./pages/Admin/anomalie/AnomalieUpdate.tsx";
import CreateUtilisateur from "./pages/Admin/utilisateur/CreateUtilisateur.tsx";
import UpdateUtilisateur from "./pages/Admin/utilisateur/UpdateUtilisateur.tsx";
import AdminAccueil from "./pages/Admin/accueil/AdminAccueil.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  LIST_INDISPONIBILITE_TEMPORAIRE: url`/deci/indisponibilite-temporaire/`,
  VIEW_COURRIER: url`/create-courrier/view-courrier`,

  // Module DECI
  DECI_CARTE: url`/deci/carte`,

  PEI: url`/deci/pei`,
  CREATE_INDISPONIBILITE_TEMPORAIRE: url`/deci/indisponibilite-temporaire/create`,
  UPDATE_INDISPONIBILITE_TEMPORAIRE: (indisponibiliteTemporaireId: string) =>
    url`/deci/indisponibilite-temporaire/` + indisponibiliteTemporaireId,

  UPDATE_PEI: (peiId: string) => url`/deci/pei/` + peiId,
  UPDATE_PENA_ASPIRATION: (peiId: string) =>
    url`/deci/pena-aspiration/` + peiId,
  VISITE: (peiId: string) => url`/deci/visite/` + peiId,
  LIST_TOURNEE: url`/deci/tournee`,
  CREATE_TOURNEE: url`/deci/tournee/create`,
  UPDATE_TOURNEE: (tourneeId: string) => url`/deci/tournee/update/` + tourneeId,
  TOURNEE_PEI: (tourneeId: string) => url`/deci/tournee/pei/` + tourneeId,
  TOURNEE_VISITE: (tourneeId: string) =>
    url`/deci/tournee/visite-tournee/` + tourneeId,

  // Module couverture hydraulique
  CREATE_ETUDE: url`/couverture-hydraulique/etudes/create`,
  UPDATE_ETUDE: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/` + etudeId,
  LIST_ETUDE: url`/couverture-hydraulique/etudes/`,
  IMPORTER_COUVERTURE_HYDRAULIQUE: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/import/` + etudeId,
  UPDATE_PEI_PROJET: (etudeId: string, peiProjetId: string) =>
    url`/couverture-hydraulique/etudes/` +
    etudeId +
    `/pei-projet/` +
    peiProjetId,
  OUVRIR_ETUDE: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/open/` + etudeId,

  // MODULE ADMIN
  MODULE_ADMIN: url`/admin/menu`,
  UPDATE_DIAMETRE: (diametreId: string) => url`/admin/diametre/` + diametreId,
  LIST_DIAMETRE: url`/admin/diametre/`,
  ADD_DIAMETRE: url`/admin/diametre/create/`,
  ADD_NATURE: url`/admin/nature/create`,
  UPDATE_NATURE: (natureId: string) => url`/admin/nature/update/` + natureId,
  LIST_NATURE: url`/admin/nature`,
  ADD_ORGANISME: url`/admin/organisme/create/`,
  UPDATE_ORGANISME: (organismeId: string) =>
    url`/admin/organisme/update/` + organismeId,
  LIST_ORGANISME: url`/admin/organisme`,

  ADD_NATURE_DECI: url`/admin/nature-deci/create`,
  UPDATE_NATURE_DECI: (natureDeciId: string) =>
    url`/admin/nature-deci/update/` + natureDeciId,
  LIST_NATURE_DECI: url`/admin/nature-deci`,

  ADD_DOMAINE: url`/admin/domaine/create`,
  UPDATE_DOMAINE: (domaineId: string) =>
    url`/admin/domaine/update/` + domaineId,
  LIST_DOMAINE: url`/admin/domaine`,

  ADD_MARQUE_PIBI: url`/admin/marque-pibi/create`,
  UPDATE_MARQUE_PIBI: (marquePibiId: string) =>
    url`/admin/marque-pibi/update/` + marquePibiId,
  LIST_MARQUE_PIBI: url`/admin/marque-pibi`,

  ADD_MATERIAU: url`/admin/materiau/create`,
  UPDATE_MATERIAU: (materiauId: string) =>
    url`/admin/materiau/update/` + materiauId,
  LIST_MATERIAU: url`/admin/materiau`,

  ADD_TYPE_CANALISATION: url`/admin/type-canalisation/create`,
  UPDATE_TYPE_CANALISATION: (typeCanalisationId: string) =>
    url`/admin/type-canalisation/update/` + typeCanalisationId,
  LIST_TYPE_CANALISATION: url`/admin/type-canalisation`,

  ADD_TYPE_RESEAU: url`/admin/type-reseau/create`,
  UPDATE_TYPE_RESEAU: (typeReseauId: string) =>
    url`/admin/type-reseau/update/` + typeReseauId,
  LIST_TYPE_RESEAU: url`/admin/type-reseau`,

  ADD_NIVEAU: url`/admin/niveau/create`,
  UPDATE_NIVEAU: (niveauId: string) => url`/admin/niveau/update/` + niveauId,
  LIST_NIVEAU: url`/admin/niveau`,

  ADD_MODELE_PIBI: url`/admin/modele-pibi/create`,
  UPDATE_MODELE_PIBI: (modelePibiId: string) =>
    url`/admin/modele-pibi/update/` + modelePibiId,
  LIST_MODELE_PIBI: url`/admin/modele-pibi`,

  ADD_TYPE_ETUDE: url`/admin/type-etude/create`,
  UPDATE_TYPE_ETUDE: (typeEtudeId: string) =>
    url`/admin/type-etude/update/` + typeEtudeId,
  LIST_TYPE_ETUDE: url`/admin/type-etude`,

  ADD_TYPE_PENA_ASPIRATION: url`/admin/type-pena-aspiration/create`,
  UPDATE_TYPE_PENA_ASPIRATION: (typePenaAspirationId: string) =>
    url`/admin/type-pena-aspiration/update/` + typePenaAspirationId,
  LIST_TYPE_PENA_ASPIRATION: url`/admin/type-pena-aspiration`,

  ADD_TYPE_ORGANISME: url`/admin/type-organisme/create`,
  UPDATE_TYPE_ORGANISME: (typeOrganismeId: string) =>
    url`/admin/type-organisme/update/` + typeOrganismeId,
  LIST_TYPE_ORGANSIME: url`/admin/type-organisme`,

  ADD_PROFIL_ORGANISME: url`/admin/profil-organisme/create`,
  UPDATE_PROFIL_ORGANISME: (profilOrganismeId: string) =>
    url`/admin/profil-organisme/update/` + profilOrganismeId,
  LIST_PROFIL_ORGANSIME: url`/admin/profil-organisme`,

  ADD_PROFIL_UTILISATEUR: url`/admin/profil-utilisateur/create`,
  UPDATE_PROFIL_UTILISATEUR: (profilUtilisateurId: string) =>
    url`/admin/profil-utilisateur/update/` + profilUtilisateurId,
  LIST_PROFIL_UTILISATEUR: url`/admin/profil-utilisateur`,

  ADD_THEMATIQUE: url`/admin/thematique/create`,
  UPDATE_THEMATIQUE: (thematiqueId: string) =>
    url`/admin/thematique/update/` + thematiqueId,
  LIST_THEMATIQUE: url`/admin/thematique`,

  ADD_ROLE_CONTACT: url`/admin/role-contact/create`,
  UPDATE_ROLE_CONTACT: (roleContactId: string) =>
    url`/admin/role-contact/update/` + roleContactId,
  LIST_ROLE_CONTACT: url`/admin/role-contact`,

  LIST_SITE: url`/admin/site`,
  UPDATE_SITE: (siteId: string) => url`/admin/site/update/` + siteId,

  LIST_GESTIONNAIRE: url`/admin/gestionnaire`,
  ADD_GESTIONNAIRE: url`/admin/gestionnaire/create`,
  UPDATE_GESTIONNAIRE: (gestionnaireId: string) =>
    url`/admin/gestionnaire/update/` + gestionnaireId,

  ADD_CONTACT: (appartenanceId: string, appartenance: string) =>
    url`/admin/` + appartenance + `/` + appartenanceId + `/contact/create/`,
  LIST_CONTACT: (appartenanceId: string, appartenance: string) =>
    url`/admin/` + appartenance + `/` + appartenanceId + `/contact/`,
  UPDATE_CONTACT: (
    appartenanceId: string,
    contactId: string,
    appartenance: string,
  ) =>
    url`/admin/` +
    appartenance +
    `/` +
    appartenanceId +
    `/contact/update/` +
    contactId,

  ADD_BLOC_DOCUMENT: url`/admin/bloc-document/create`,
  LIST_BLOC_DOCUMENT: url`/admin/bloc-document`,
  UPDATE_BLOC_DOCUMENT: (blocDocumentId: string) =>
    url`/admin/bloc-document/update/` + blocDocumentId,

  ADD_UTILISATEUR: url`/admin/utilisateur/create`,
  LIST_UTILISATEUR: url`/admin/utilisateur`,
  UPDATE_UTILISATEUR: (utilisateurId: string) =>
    url`/admin/utilisateur/update/` + utilisateurId,

  ADMIN_FICHE_RESUME: url`/admin/fiche-resume`,
  ADMIN_ACCUEIL: url`/admin/module-accueil`,
};

// On définit les routes par module pour que les enfants héritent du header ou d'autres éléments

export default [
  {
    path: "/",
    element: (
      <Authorization Component={Accueil} droits={Object.values(TYPE_DROIT)} />
    ),
  },
  {
    path: "/deci/",
    element: (
      <Authorization
        Component={ModuleDeci}
        droits={Object.values(TYPE_DROIT)}
      />
    ),
    children: [
      {
        path: "pei",
        element: (
          <Authorization Component={AccueilPei} droits={[TYPE_DROIT.PEI_R]} />
        ),
      },
      {
        path: "pei/:peiId",
        element: (
          <Authorization
            Component={UpdatePei}
            droits={[
              TYPE_DROIT.PEI_U,
              TYPE_DROIT.PEI_CARACTERISTIQUES_U,
              TYPE_DROIT.PEI_NUMERO_INTERNE_U,
              TYPE_DROIT.PEI_DEPLACEMENT_U,
            ]}
          />
        ),
      },
      {
        path: "pei/create",
        element: (
          <Authorization Component={CreatePei} droits={[TYPE_DROIT.PEI_C]} />
        ),
      },
      {
        path: "pena-aspiration/:penaId",
        element: (
          <Authorization
            Component={AireAspiration}
            droits={[TYPE_DROIT.PEI_CARACTERISTIQUES_U, TYPE_DROIT.PEI_U]}
          />
        ),
      },
      {
        path: "carte",
        element: <Authorization Component={MapPei} isPublic={true} />,
      },
      {
        path: "tournee",
        element: (
          <Authorization
            Component={ListTournee}
            droits={[TYPE_DROIT.TOURNEE_R, TYPE_DROIT.TOURNEE_A]}
          />
        ),
      },
      {
        path: "tournee/create",
        element: (
          <Authorization
            Component={CreateTournee}
            droits={[TYPE_DROIT.TOURNEE_A]}
          />
        ),
      },
      {
        path: "tournee/update/:tourneeId",
        element: (
          <Authorization
            Component={UpdateTournee}
            droits={[TYPE_DROIT.TOURNEE_A]}
          />
        ),
      },
      {
        path: "tournee/pei/:tourneeId",
        element: (
          <Authorization
            Component={TourneePei}
            droits={[TYPE_DROIT.TOURNEE_A]}
          />
        ),
      },
      {
        path: "tournee/visite-tournee/:tourneeId",
        element: (
          <Authorization
            Component={ValidateAccessSaisieVisiteTournee}
            droits={[TYPE_DROIT.TOURNEE_R, TYPE_DROIT.TOURNEE_A]}
          />
        ),
      },
      {
        path: "visite/:peiId",
        element: (
          <Authorization Component={Visite} droits={[TYPE_DROIT.VISITE_R]} />
        ),
      },
      {
        path: "indisponibilite-temporaire/create",
        element: (
          <Authorization
            Component={CreateIndisponibiliteTemporaire}
            droits={[TYPE_DROIT.INDISPO_TEMP_C]}
          />
        ),
      },
      {
        path: "indisponibilite-temporaire/",
        element: (
          <Authorization
            Component={ListIndisponibiliteTemporaire}
            droits={[TYPE_DROIT.INDISPO_TEMP_R]}
          />
        ),
      },
      {
        path: "indisponibilite-temporaire/:indisponibiliteTemporaireId",
        element: (
          <Authorization
            Component={UpdateIndisponibiliteTemporaire}
            droits={[TYPE_DROIT.INDISPO_TEMP_U]}
          />
        ),
      },
    ],
  },
  {
    path: "/couverture-hydraulique/",
    element: <ModuleCouvertureHydraulique />,
    children: [
      {
        path: "etudes",
        element: (
          <Authorization Component={ListEtude} droits={[TYPE_DROIT.ETUDE_R]} />
        ),
      },
      {
        path: "etudes/create",
        element: (
          <Authorization
            Component={CreateEtude}
            droits={[TYPE_DROIT.ETUDE_C]}
          />
        ),
      },
      {
        path: "etudes/:etudeId",
        element: (
          <Authorization
            Component={UpdateEtude}
            droits={[TYPE_DROIT.ETUDE_U]}
          />
        ),
      },
      {
        path: "etudes/open/:etudeId",
        element: (
          <Authorization
            Component={MapEtude}
            droits={[
              TYPE_DROIT.ETUDE_U,
              TYPE_DROIT.ETUDE_R,
              TYPE_DROIT.ETUDE_D,
              TYPE_DROIT.ETUDE_C,
            ]}
          />
        ),
      },
      {
        path: "etudes/import/:etudeId",
        element: (
          <Authorization
            Component={ImportShapeEtude}
            droits={[TYPE_DROIT.ETUDE_U]}
          />
        ),
      },
      {
        path: "etudes/:etudeId/pei-projet/:peiProjetId",
        element: (
          <Authorization
            Component={UpdatePeiProjet}
            droits={[TYPE_DROIT.ETUDE_U]}
          />
        ),
      },
    ],
  },
  {
    path: "/create-courrier/",
    element: <GenereCourrier />,
    children: [
      {
        path: "view-courrier",
        element: (
          <Authorization
            Component={ViewCourrier}
            droits={[TYPE_DROIT.COURRIER_C]}
          />
        ),
      },
    ],
  },

  {
    path: "/admin/",
    element: (
      <Authorization
        Component={ModuleAdmin}
        droits={Object.values(TYPE_DROIT)}
      />
    ),
    children: [
      {
        path: "menu",
        element: (
          <Authorization
            Component={MenuAdmin}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "diametre",
        element: (
          <Authorization
            Component={ListDiametre}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "diametre/:diametreId",
        element: (
          <Authorization
            Component={UpdateDiametre}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "diametre/create",
        element: (
          <Authorization
            Component={CreateDiametre}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature/",
        element: (
          <Authorization
            Component={ListNature}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature/update/:natureId",
        element: (
          <Authorization
            Component={UpdateNature}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature/create",
        element: (
          <Authorization
            Component={CreateNature}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature-deci/",
        element: (
          <Authorization
            Component={ListNatureDeci}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature-deci/update/:natureDeciId",
        element: (
          <Authorization
            Component={UpdateNatureDeci}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "nature-deci/create",
        element: (
          <Authorization
            Component={CreateNatureDeci}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "domaine",
        element: (
          <Authorization
            Component={ListDomaine}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "domaine/update/:domaineId",
        element: (
          <Authorization
            Component={UpdateDomaine}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "domaine/create",
        element: (
          <Authorization
            Component={CreateDomaine}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "marque-pibi",
        element: (
          <Authorization
            Component={ListMarquePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "marque-pibi/update/:marquePibiId",
        element: (
          <Authorization
            Component={UpdateMarquePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "marque-pibi/create",
        element: (
          <Authorization
            Component={CreateMarquePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "materiau",
        element: (
          <Authorization
            Component={ListMateriau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "materiau/update/:materiauId",
        element: (
          <Authorization
            Component={UpdateMateriau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "materiau/create",
        element: (
          <Authorization
            Component={CreateMateriau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-canalisation",
        element: (
          <Authorization
            Component={ListTypeCanalisation}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-canalisation/update/:typeCanalisationId",
        element: (
          <Authorization
            Component={UpdateTypeCanalisation}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-canalisation/create",
        element: (
          <Authorization
            Component={CreateTypeCanalisation}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-reseau",
        element: (
          <Authorization
            Component={ListTypeReseau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-reseau/update/:typeReseauId",
        element: (
          <Authorization
            Component={UpdateTypeReseau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-reseau/create",
        element: (
          <Authorization
            Component={CreateTypeReseau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },

      {
        path: "niveau",
        element: (
          <Authorization
            Component={ListNiveau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "niveau/update/:niveauId",
        element: (
          <Authorization
            Component={UpdateNiveau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "niveau/create",
        element: (
          <Authorization
            Component={CreateNiveau}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "modele-pibi",
        element: (
          <Authorization
            Component={ListModelePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "modele-pibi/update/:modelePibiId",
        element: (
          <Authorization
            Component={UpdateModelePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "modele-pibi/create",
        element: (
          <Authorization
            Component={CreateModelePibi}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-etude",
        element: (
          <Authorization
            Component={ListTypeEtude}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-etude/update/:typeEtudeId",
        element: (
          <Authorization
            Component={UpdateTypeEtude}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-etude/create",
        element: (
          <Authorization
            Component={CreateTypeEtude}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-pena-aspiration",
        element: (
          <Authorization
            Component={ListTypePenaAspiration}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-pena-aspiration/update/:typePenaAspirationId",
        element: (
          <Authorization
            Component={UpdateTypePenaAspiration}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-pena-aspiration/create",
        element: (
          <Authorization
            Component={CreateTypePenaAspiration}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },

      {
        path: "thematique",
        element: (
          <Authorization
            Component={ListThematique}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "thematique/update/:thematiqueId",
        element: (
          <Authorization
            Component={UpdateThematique}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "thematique/create",
        element: (
          <Authorization
            Component={CreateThematique}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "organisme",
        element: (
          <Authorization
            Component={ListOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "organisme/create",
        element: (
          <Authorization
            Component={CreateOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "organisme/update/:organismeId",
        element: (
          <Authorization
            Component={UpdateOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-organisme",
        element: (
          <Authorization
            Component={ListTypeOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-organisme/create",
        element: (
          <Authorization
            Component={CreateTypeOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "type-organisme/update/:typeOrganismeId",
        element: (
          <Authorization
            Component={UpdateTypeOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-organisme",
        element: (
          <Authorization
            Component={ListProfilOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-organisme/create",
        element: (
          <Authorization
            Component={CreateProfilOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-organisme/update/:profilOrganismeId",
        element: (
          <Authorization
            Component={UpdateProfilOrganisme}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-utilisateur",
        element: (
          <Authorization
            Component={ListProfilUtilisateur}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-utilisateur/create",
        element: (
          <Authorization
            Component={CreateProfilUtilisateur}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "role-contact/update/:roleContactId",
        element: (
          <Authorization
            Component={UpdateRoleContact}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "role-contact",
        element: (
          <Authorization
            Component={ListRoleContact}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "role-contact/create",
        element: (
          <Authorization
            Component={CreateRoleContact}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "profil-utilisateur/update/:profilUtilisateurId",
        element: (
          <Authorization
            Component={UpdateProfilUtilisateur}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "site",
        element: (
          <Authorization
            Component={ListSite}
            droits={[TYPE_DROIT.GEST_SITE_R]}
          />
        ),
      },
      {
        path: "site/update/:siteId",
        element: (
          <Authorization
            Component={UpdateSite}
            droits={[TYPE_DROIT.GEST_SITE_A]}
          />
        ),
      },
      {
        path: "gestionnaire",
        element: (
          <Authorization
            Component={ListGestionnaire}
            droits={[TYPE_DROIT.GEST_SITE_R]}
          />
        ),
      },
      {
        path: "gestionnaire/update/:gestionnaireId",
        element: (
          <Authorization
            Component={UpdateGestionnaire}
            droits={[TYPE_DROIT.GEST_SITE_A]}
          />
        ),
      },
      {
        path: "gestionnaire/create",
        element: (
          <Authorization
            Component={CreateGestionnaire}
            droits={[TYPE_DROIT.GEST_SITE_A]}
          />
        ),
      },
      {
        path: ":appartenance/:appartenanceId/contact/create",
        element: (
          <Authorization
            Component={CreateContact}
            droits={[TYPE_DROIT.GEST_SITE_A, TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: ":appartenance/:appartenanceId/contact",
        element: (
          <Authorization
            Component={ListContact}
            droits={[TYPE_DROIT.GEST_SITE_R, TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: ":appartenance/:appartenanceId/contact/update/:contactId",
        element: (
          <Authorization
            Component={UpdateContact}
            droits={[TYPE_DROIT.GEST_SITE_A, TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "bloc-document",
        element: (
          <Authorization
            Component={ListBlocDocument}
            droits={[TYPE_DROIT.DOCUMENTS_R]}
          />
        ),
      },
      {
        path: "bloc-document/create",
        element: (
          <Authorization
            Component={CreateBlocDocument}
            droits={[TYPE_DROIT.DOCUMENTS_A]}
          />
        ),
      },
      {
        path: "bloc-document/update/:blocDocumentId",
        element: (
          <Authorization
            Component={UpdateBlocDocument}
            droits={[TYPE_DROIT.DOCUMENTS_A]}
          />
        ),
      },
      {
        path: "anomalie",
        element: (
          <Authorization
            Component={AnomalieList}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "anomalie/create",
        element: (
          <Authorization
            Component={AnomalieCreate}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "anomalie/:anomalieId",
        element: (
          <Authorization
            Component={AnomalieUpdate}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "utilisateur",
        element: (
          <Authorization
            Component={ListUtilisateur}
            droits={[TYPE_DROIT.ADMIN_UTILISATEURS_R]}
          />
        ),
      },
      {
        path: "utilisateur/create",
        element: (
          <Authorization
            Component={CreateUtilisateur}
            droits={[TYPE_DROIT.ADMIN_UTILISATEURS_A]}
          />
        ),
      },
      {
        path: "utilisateur/update/:utilisateurId",
        element: (
          <Authorization
            Component={UpdateUtilisateur}
            droits={[TYPE_DROIT.ADMIN_UTILISATEURS_A]}
          />
        ),
      },
      {
        path: "fiche-resume",
        element: (
          <Authorization
            Component={AdminFicheResume}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
      {
        path: "module-accueil",
        element: (
          <Authorization
            Component={AdminAccueil}
            droits={[TYPE_DROIT.ADMIN_DROITS]}
          />
        ),
      },
    ],
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
