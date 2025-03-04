import { Outlet } from "react-router-dom";
import { hasDroit } from "../../droits.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import { TypeModuleRemocra } from "./ModuleRemocra.tsx";

const ModuleDeci = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.PEI,
      label: "Points d'eau",
      aLeDroit: hasDroit(user, TYPE_DROIT.PEI_R),
    },
    {
      path: URLS.LIST_TOURNEE,
      label: "Tournées",
      aLeDroit: hasDroit(user, TYPE_DROIT.TOURNEE_R),
    },
    {
      path: URLS.LIST_INDISPONIBILITE_TEMPORAIRE,
      label: "Indisponibilités temporaires",
      aLeDroit: hasDroit(user, TYPE_DROIT.INDISPO_TEMP_R),
    },
    {
      path: URLS.DECI_CARTE,
      label: "Carte",
      aLeDroit: true,
    },
    {
      path: URLS.GENERER_COURRIER(TypeModuleRemocra.DECI),
      label: "Générer un courrier",
      aLeDroit: hasDroit(user, TYPE_DROIT.COURRIER_C),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleDeci;
