import { Outlet } from "react-router-dom";
import Header, { NavToProps } from "../Header/Header.tsx";
import { URLS } from "../../routes.tsx";

const ModuleCouvertureHydraulique = () => {
  const navLinks: NavToProps[] = [
    { path: URLS.LIST_ETUDE, label: "Liste des études" },
  ];

  return (
    <>
      <Header links={navLinks} />
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </>
  );
};

export default ModuleCouvertureHydraulique;
