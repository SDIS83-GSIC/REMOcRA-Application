import { Outlet } from "react-router-dom";
import Header, { NavToProps } from "../Header/Header.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const ModuleOldeb = () => {
  const navLinks: NavToProps[] = [
    {
      path: URLS.OLDEB_LIST,
      label: "Liste des Obligations Légales de Débroussaillement",
    },
    {
      path: URLS.OLDEB_PROPRIETAIRE_LIST,
      label: "Propriétaires",
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleOldeb;
