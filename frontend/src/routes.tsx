import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import UpdatePei from "./pages/Pei/UpdatePei.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
  UPDATE_PEI: (peiId: string) => url`/pei/update/` + peiId,
};

export default [
  {
    path: "/pei/",
    element: <AccueilPei />,
  },
  {
    path: "/pei/update/:peiId",
    element: <UpdatePei />,
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
