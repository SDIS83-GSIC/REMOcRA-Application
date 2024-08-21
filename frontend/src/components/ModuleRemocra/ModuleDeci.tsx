import { Outlet } from "react-router-dom";
import { URLS } from "../../routes.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleDeci = () => {
  const navLinks: NavToProps[] = [{ path: URLS.PEI, label: "Points d'eau" }];

  return (
    <>
      <Header links={navLinks} />
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </>
  );
};

export default ModuleDeci;
