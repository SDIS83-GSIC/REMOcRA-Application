import { BrowserRouter } from "react-router-dom";
import { AppProvider } from "./components/App/AppProvider.tsx";
import "./App.module.css";
import routes from "./routes.tsx";
import RouteConfig from "./components/Router/RouteConfig.tsx";
import "bootstrap/dist/css/bootstrap.min.css";
import { DataCacheProvider } from "./components/App/DataCacheContext.tsx";

/**
 * Ce fichier est le point d'entrée pour la partie authentifiée du site. Le userInfo ne doit jamais être nul.
 */
const App = () => {
  return (
    <AppProvider>
      <DataCacheProvider>
        <BrowserRouter>
          <h1>Page authentifiée</h1>
          <RouteConfig routes={routes} />
        </BrowserRouter>
      </DataCacheProvider>
    </AppProvider>
  );
};
export default App;
