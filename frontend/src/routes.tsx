import MapPei from "./components/Map/MapPei/MapPei.tsx";
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
import CreateMarquePibi from "./pages/Admin/marquePibi/CreateMarquePibi.tsx";
import ListMarquePibi from "./pages/Admin/marquePibi/ListMarquePibi.tsx";
import UpdateMarquePibi from "./pages/Admin/marquePibi/UpdateMarquePibi.tsx";
import CreateMateriau from "./pages/Admin/materiau/CreateMateriau.tsx";
import ListMateriau from "./pages/Admin/materiau/ListMateriau.tsx";
import UpdateMateriau from "./pages/Admin/materiau/UpdateMateriau.tsx";
import CreateNature from "./pages/Admin/nature/CreateNature.tsx";
import ListNature from "./pages/Admin/nature/ListNature.tsx";
import UpdateNature from "./pages/Admin/nature/UpdateNature.tsx";
import CreateNatureDeci from "./pages/Admin/natureDeci/CreateNatureDeci.tsx";
import ListNatureDeci from "./pages/Admin/natureDeci/ListNatureDeci.tsx";
import UpdateNatureDeci from "./pages/Admin/natureDeci/UpdateNatureDeci.tsx";
import CreateOrganisme from "./pages/Admin/organisme/CreateOrganisme.tsx";
import ListOrganisme from "./pages/Admin/organisme/ListOrganisme.tsx";
import UpdateOrganisme from "./pages/Admin/organisme/UpdateOrganisme.tsx";
import CreateTypeCanalisation from "./pages/Admin/typeCanalisation/CreateTypeCanalisation.tsx";
import ListTypeCanalisation from "./pages/Admin/typeCanalisation/ListTypeCanalisation.tsx";
import UpdateTypeCanalisation from "./pages/Admin/typeCanalisation/UpdateTypeCanalisation.tsx";
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

  UPDATE_DIAMETRE: (diametreId: string) => url`/admin/diametre/` + diametreId,
  DIAMETRE: url`/admin/diametre/`,
  ADD_DIAMETRE: url`/admin/diametre/add/`,
  ADD_NATURE: url`/admin/nature/add`,
  UPDATE_NATURE: (natureId: string) => url`/admin/nature/edit/` + natureId,
  LIST_NATURE: url`/admin/nature`,
  ADD_ORGANISME: url`/admin/organisme/add/`,
  UPDATE_ORGANISME: (organismeId: string) =>
    url`/admin/organisme/edit/` + organismeId,
  ORGANISME: url`/admin/organisme`,

  ADD_NATURE_DECI: url`/admin/nature-deci/add`,
  UPDATE_NATURE_DECI: (natureDeciId: string) =>
    url`/admin/nature-deci/edit/` + natureDeciId,
  LIST_NATURE_DECI: url`/admin/nature-deci`,

  ADD_DOMAINE: url`/admin/domaine/add`,
  UPDATE_DOMAINE: (domaineId: string) => url`/admin/domaine/edit/` + domaineId,
  LIST_DOMAINE: url`/admin/domaine`,

  ADD_MARQUE_PIBI: url`/admin/marque-pibi/add`,
  UPDATE_MARQUE_PIBI: (marquePibiId: string) =>
    url`/admin/marque-pibi/edit/` + marquePibiId,
  LIST_MARQUE_PIBI: url`/admin/marque-pibi`,

  ADD_MATERIAU: url`/admin/materiau/add`,
  UPDATE_MATERIAU: (materiauId: string) =>
    url`/admin/materiau/edit/` + materiauId,
  LIST_MATERIAU: url`/admin/materiau`,

  ADD_TYPE_CANALISATION: url`/admin/type-canalisation/add`,
  UPDATE_TYPE_CANALISATION: (typeCanalisationId: string) =>
    url`/admin/type-canalisation/edit/` + typeCanalisationId,
  LIST_TYPE_CANALISATION: url`/admin/type-canalisation`,
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
    path: "/admin/diametre/",
    element: (
      <Authorization
        Component={ListDiametre}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/diametre/:diametreId",
    element: (
      <Authorization
        Component={UpdateDiametre}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/diametre/add",
    element: (
      <Authorization
        Component={CreateDiametre}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature/",
    element: (
      <Authorization
        Component={ListNature}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature/edit/:natureId",
    element: (
      <Authorization
        Component={UpdateNature}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature/add",
    element: (
      <Authorization
        Component={CreateNature}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature-deci/",
    element: (
      <Authorization
        Component={ListNatureDeci}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature-deci/edit/:natureDeciId",
    element: (
      <Authorization
        Component={UpdateNatureDeci}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/nature-deci/add",
    element: (
      <Authorization
        Component={CreateNatureDeci}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/domaine",
    element: (
      <Authorization
        Component={ListDomaine}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/domaine/edit/:domaineId",
    element: (
      <Authorization
        Component={UpdateDomaine}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/domaine/add",
    element: (
      <Authorization
        Component={CreateDomaine}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/marque-pibi",
    element: (
      <Authorization
        Component={ListMarquePibi}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/marque-pibi/edit/:marquePibiId",
    element: (
      <Authorization
        Component={UpdateMarquePibi}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/marque-pibi/add",
    element: (
      <Authorization
        Component={CreateMarquePibi}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/materiau",
    element: (
      <Authorization
        Component={ListMateriau}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/materiau/edit/:materiauId",
    element: (
      <Authorization
        Component={UpdateMateriau}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/materiau/add",
    element: (
      <Authorization
        Component={CreateMateriau}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/type-canalisation",
    element: (
      <Authorization
        Component={ListTypeCanalisation}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/type-canalisation/edit/:typeCanalisationId",
    element: (
      <Authorization
        Component={UpdateTypeCanalisation}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/type-canalisation/add",
    element: (
      <Authorization
        Component={CreateTypeCanalisation}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/organisme",
    element: (
      <Authorization
        Component={ListOrganisme}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/organisme/add",
    element: (
      <Authorization
        Component={CreateOrganisme}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "/admin/organisme/edit/:organismeId",
    element: (
      <Authorization
        Component={UpdateOrganisme}
        droits={[TYPE_DROIT.ADMIN_DROITS]}
      />
    ),
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
