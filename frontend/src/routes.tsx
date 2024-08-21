import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import CreatePei from "./pages/Pei/CreatePei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";
import Visite from "./pages/Visite/Visite.tsx";
import Accueil from "./pages/Accueil/Accueil.tsx";
import ModuleDeci from "./components/ModuleRemocra/ModuleDeci.tsx";
import GenereCourrier from "./pages/Courrier/GenereCourrier.tsx";
import ViewCourrier from "./pages/Courrier/ViewCourrier.tsx";

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
    element: <Accueil />,
  },
  {
    path: "/deci/",
    element: <ModuleDeci />,
    children: [
      { path: "pei", element: <AccueilPei /> },
      {
        path: "pei/update/:peiId",
        element: <UpdatePei />,
      },
      {
        path: "/deci/pei/update/:peiId",
        element: <UpdatePei />,
      },
      {
        path: "deci/pei/create",
        element: <CreatePei />,
      },
      {
        path: "/deci/pena-aspiration/:penaId",
        element: <AireAspiration />,
      },
      {
        path: "/deci/visite/:peiId",
        element: <Visite />,
      },
    ],
  },
  {
    path: "/create-courrier/",
    element: <GenereCourrier />,
    children: [{ path: "view-courrier", element: <ViewCourrier /> }],
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
