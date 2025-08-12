import { Outlet } from "react-router-dom";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import Header, { NavToProps } from "../../components/Header/Header.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import { URLS } from "../../routes.tsx";
import SquelettePage from "../SquelettePage.tsx";

const ModuleAdresse = () => {
  const { user } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.ADRESSE,
      label: "Carte des adresses",
      aLeDroit: hasDroit(user, TYPE_DROIT.ADRESSES_C),
    },
    {
      path: URLS.ADRESSE_DELIBERATION,
      label: "Déposer une délibération",
      aLeDroit: hasDroit(user, TYPE_DROIT.DEPOT_DELIB_C),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleAdresse;
