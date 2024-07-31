import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import CreatePei from "./pages/Pei/CreatePei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";
import Visite from "./pages/Visite/Visite.tsx";
import Accueil from "./pages/Accueil/Accueil.tsx";
import ModuleDeci from "./components/ModuleRemocra/ModuleDeci.tsx";
import ListEtude from "./pages/Etude/ListEtude.tsx";
import GenereCourrier from "./pages/Courrier/GenereCourrier.tsx";
import ViewCourrier from "./pages/Courrier/ViewCourrier.tsx";
import GestionTournee from "./pages/Tournee/Tournee.tsx";
import { Authorization } from "./droits.tsx";
import { TYPE_DROIT } from "./Entities/UtilisateurEntity.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  VIEW_COURRIER: url`/create-courrier/view-courrier`,
  PEI: url`/deci/pei`,
  UPDATE_PEI: (peiId: string) => url`/deci/pei/` + peiId,
  UPDATE_PENA_ASPIRATION: (peiId: string) =>
    url`/deci/pena-aspiration/` + peiId,
  VISITE: (peiId: string) => url`/deci/visite/` + peiId,
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
        path: "tournee",
        element: (
          <Authorization
            Component={GestionTournee}
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
        path: "etudes",
        element: (
          <Authorization Component={ListEtude} droits={[TYPE_DROIT.ETUDE_R]} />
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
    path: "*",
    element: <NoMatch />,
  },
];
