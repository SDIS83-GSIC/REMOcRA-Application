import { Outlet } from "react-router-dom";
import Header, { NavToProps } from "../Header/Header.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import { useAppContext } from "../App/AppProvider.tsx";

const ModuleCrise = () => {
  const { user } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.LIST_CRISES,
      label: "Liste des crises",
      aLeDroit: hasDroit(user, TYPE_DROIT.CRISE_R),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleCrise;
