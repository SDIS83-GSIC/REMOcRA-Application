import { Outlet } from "react-router-dom";
import { URLS } from "../../routes.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const ModuleOperationsDiverses = () => {
  const navLinks: NavToProps[] = [
    {
      path: URLS.HISTORIQUE_OPERATIONS,
      label: "Historique des opérations",
    },
    {
      path: URLS.RESULTATS_EXECUTION,
      label: "Résultats d'exécutions",
    },
  ];

  return (
    <SquelettePage fluid navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleOperationsDiverses;
