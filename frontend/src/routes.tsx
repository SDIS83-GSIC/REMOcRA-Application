import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";
import AireAspiration from "./pages/Pena/AireAspiration.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  PEI: url`/pei`,
  UPDATE_PEI: (peiId: string) => url`/pei/update/` + peiId,
  UPDATE_PENA_ASPIRATION: (peiId: string) => url`/pena-aspiration/` + peiId,
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
    path: "/pena-aspiration/:penaId",
    element: <AireAspiration />,
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
