import { Outlet } from "react-router-dom";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { isAuthorized } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleAdmin = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.MODULE_ADMIN,
      label: "Administrer",
      aLeDroit: isAuthorized(user, Object.values(TYPE_DROIT)),
    },
  ];
  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleAdmin;
