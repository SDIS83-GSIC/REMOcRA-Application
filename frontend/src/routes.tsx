import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import CreatePei from "./pages/Pei/CreatePei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";
import Visite from "./pages/Visite/Visite.tsx";
import Accueil from "./pages/Accueil/Accueil.tsx";
import ModuleDeci from "./components/ModuleRemocra/ModuleDeci.tsx";
import ListEtude from "./pages/CouvertureHydraulique/Etude/ListEtude.tsx";
import CreateEtude from "./pages/CouvertureHydraulique/Etude/CreateEtude.tsx";
import UpdateEtude from "./pages/CouvertureHydraulique/Etude/UpdateEtude.tsx";
import GenereCourrier from "./pages/Courrier/GenereCourrier.tsx";
import ViewCourrier from "./pages/Courrier/ViewCourrier.tsx";
import ImportShapeEtude from "./pages/CouvertureHydraulique/Etude/ImportShapeEtude.tsx";
import ListIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/ListIndisponibiliteTemporaire.tsx";
import CreateIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/CreateIndisponibiliteTemporaire.tsx";
import ListTournee from "./pages/Tournee/ListTournee.tsx";
import CreateTournee from "./pages/Tournee/CreateTournee.tsx";
import UpdateTournee from "./pages/Tournee/UpdateTournee.tsx";
import TourneePei from "./pages/Tournee/TourneePei.tsx";
import { Authorization } from "./droits.tsx";
import { TYPE_DROIT } from "./Entities/UtilisateurEntity.tsx";
import CreatePeiProjet from "./pages/CouvertureHydraulique/PeiProjet/CreatePeiProjet.tsx";
import UpdatePeiProjet from "./pages/CouvertureHydraulique/PeiProjet/UpdatePeiProjet.tsx";
import ModuleCouvertureHydraulique from "./components/ModuleRemocra/ModuleCouvertureHydraulique.tsx";
import UpdateIndisponibiliteTemporaire from "./pages/IndisponibiliteTemporaire/UpdateIndisponibiliteTemporaire.tsx";
import MapComponent from "./components/Map/Map.tsx";
import MapEtude from "./pages/CouvertureHydraulique/Etude/MapEtude.tsx";
import FicheResume from "./pages/Pei/FicheResume/FicheResume.tsx";
import ListDiametre from "./pages/Admin/diametre/ListDiametre.tsx";
import UpdateDiametre from "./pages/Admin/diametre/UpdateDiametre.tsx";
import CreateDiametre from "./pages/Admin/diametre/CreateDiametre.tsx";
import ListNature from "./pages/Admin/nature/ListNature.tsx";
import UpdateNature from "./pages/Admin/nature/UpdateNature.tsx";
import CreateNature from "./pages/Admin/nature/CreateNature.tsx";
import ListOrganisme from "./pages/Admin/organisme/ListOrganisme.tsx";
import CreateOrganisme from "./pages/Admin/organisme/CreateOrganisme.tsx";
import UpdateOrganisme from "./pages/Admin/organisme/UpdateOrganisme.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  LIST_INDISPONIBILITE_TEMPORAIRE: url`/deci/indisponibilite-temporaire/`,
  VIEW_COURRIER: url`/create-courrier/view-courrier`,

  // Module DECI
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
  FICHE_RESUME: (peiId: string) => url`/deci/pei/fiche/` + peiId,

  // Module couverture hydraulique
  CREATE_ETUDE: url`/couverture-hydraulique/etudes/create`,
  UPDATE_ETUDE: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/` + etudeId,
  LIST_ETUDE: url`/couverture-hydraulique/etudes/`,
  IMPORTER_COUVERTURE_HYDRAULIQUE: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/import/` + etudeId,
  CREATE_PEI_PROJET: (etudeId: string) =>
    url`/couverture-hydraulique/etudes/` + etudeId + `/pei-projet/`,
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
        path: "pei/fiche/:peiId",
        element: (
          <Authorization Component={FicheResume} droits={[TYPE_DROIT.PEI_R]} />
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
        path: "pei/carte",
        element: <Authorization Component={MapComponent} isPublic={true} />,
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
        path: "etudes/:etudeId/pei-projet",
        element: (
          <Authorization
            Component={CreatePeiProjet}
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
