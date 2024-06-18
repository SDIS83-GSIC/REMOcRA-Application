import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";
import AccueilPei from "./pages/Pei/AccueilPei.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
};

export default [
  {
    path: "/pei/",
    element: <AccueilPei />,
  },
  {
    path: "*",
    element: <NoMatch />,
  },
];
