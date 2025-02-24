import { Outlet } from "react-router-dom";
import Header, { NavToProps } from "../Header/Header.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const ModuleCrise = () => {
  const navLinks: NavToProps[] = [
    { path: URLS.LIST_CRISES, label: "Liste des crises" },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleCrise;
