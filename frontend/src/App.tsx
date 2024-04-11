import { BrowserRouter } from "react-router-dom";
import { AppProvider } from "./components/App/AppProvider.tsx";
import "./App.module.css";

/**
 * Ce fichier est le point d'entrée pour la partie authentifiée du site. Le userInfo ne doit jamais être nul.
 */
const App = () => {
  return (
    <BrowserRouter>
      <AppProvider>
        <h1>Page authentifiée</h1>
      </AppProvider>
    </BrowserRouter>
  );
};
export default App;
