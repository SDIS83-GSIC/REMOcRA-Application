import { Outlet } from "react-router-dom";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleRcci = () => {
  const navLinks: NavToProps[] = [
    { path: URLS.RCCI_MAP, label: "Carte des départs de feu" },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleRcci;
