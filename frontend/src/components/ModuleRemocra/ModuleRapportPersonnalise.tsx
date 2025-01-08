import { Outlet } from "react-router-dom";
import { URLS } from "../../routes.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const ModuleRapportPersonnalise = () => {
  const navLinks: NavToProps[] = [
    {
      path: URLS.EXECUTER_RAPPORT_PERSONNALISE,
      label: "Exécuter un rapport personnalisé",
    },
  ];

  return (
    <SquelettePage fluid navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleRapportPersonnalise;
