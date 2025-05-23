import { BrowserRouter } from "react-router-dom";
import { AppProvider } from "./components/App/AppProvider.tsx";
import "./App.css";
import routes from "./routes.tsx";
import RouteConfig from "./components/Router/RouteConfig.tsx";
import { ToastProvider } from "./module/Toast/ToastProvider.tsx";
import "./style.scss";

const App = () => {
  return (
    <AppProvider>
      <ToastProvider>
        <BrowserRouter>
          <RouteConfig routes={routes} />
        </BrowserRouter>
      </ToastProvider>
    </AppProvider>
  );
};
export default App;
