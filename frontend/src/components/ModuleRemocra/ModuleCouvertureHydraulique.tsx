import { Outlet } from "react-router-dom";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleCouvertureHydraulique = () => {
  const { user } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.LIST_ETUDE,
      label: "Liste des études",
      aLeDroit: hasDroit(user, TYPE_DROIT.ETUDE_R),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleCouvertureHydraulique;
