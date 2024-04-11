import NoMatch from "./components/Router/NoMatch.tsx";
import url from "./module/fetch.tsx";

export const URLS = {
  ACCUEIL: url`/`,
  LOGOUT: url`/logout`,
};

export default [
  {
    path: "*",
    element: <NoMatch />,
  },
];
