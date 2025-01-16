import { Outlet } from "react-router-dom";
import { URLS } from "../../routes.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const ModuleDeci = () => {
  const navLinks: NavToProps[] = [
    { path: URLS.PEI, label: "Points d'eau" },
    {
      path: URLS.LIST_TOURNEE,
      label: "Tournées",
    },
    {
      path: URLS.LIST_INDISPONIBILITE_TEMPORAIRE,
      label: "Indisponibilités temporaires",
    },
    { path: URLS.DECI_CARTE, label: "Carte" },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleDeci;
