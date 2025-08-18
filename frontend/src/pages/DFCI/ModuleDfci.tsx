import { Outlet } from "react-router-dom";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import Header, { NavToProps } from "../../components/Header/Header.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../SquelettePage.tsx";

const ModuleDfci = () => {
  const { user } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.CARTE_DFCI,
      label: "Carte DFCI",
      aLeDroit: hasDroit(user, TYPE_DROIT.DFCI_R),
    },
    {
      path: URLS.DFCI_RECEPTION_TRAVAUX,
      label: "Recevoir des travaux",
      aLeDroit: hasDroit(user, TYPE_DROIT.DFCI_RECEPTRAVAUX_C),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleDfci;
