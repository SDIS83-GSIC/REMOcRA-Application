import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import CreatePei from "./pages/Pei/CreatePei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";
import Visite from "./pages/Visite/Visite.tsx";
import GenereCourrier from "./pages/Courrier/GenereCourrier.tsx";
import ViewCourrier from "./pages/Courrier/ViewCourrier.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  PEI: url`/pei`,
  UPDATE_PEI: (peiId: string) => url`/pei/` + peiId,
  UPDATE_PENA_ASPIRATION: (peiId: string) => url`/pena-aspiration/` + peiId,
  VISITE: (peiId: string) => url`/visite/` + peiId,
  VIEW_COURRIER: url`/create-courrier/view-courrier`,
};

export default [
  {
    path: "/pei",
    element: <AccueilPei />,
  },
  {
    path: "/pei/update/:peiId",
    element: <UpdatePei />,
  },
  {
    path: "/pei/create",
    element: <CreatePei />,
  },
  {
    path: "/pena-aspiration/:penaId",
    element: <AireAspiration />,
  },
  {
    path: "/visite/:peiId",
    element: <Visite />,
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
