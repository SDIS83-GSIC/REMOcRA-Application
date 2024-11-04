import { Outlet } from "react-router-dom";
import SquelettePage from "../../pages/SquelettePage.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import { URLS } from "../../routes.tsx";

const ModuleAdmin = () => {
  const navLinks: NavToProps[] = [
    { path: URLS.MODULE_ADMIN, label: "Administrer" },
  ];
  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleAdmin;
