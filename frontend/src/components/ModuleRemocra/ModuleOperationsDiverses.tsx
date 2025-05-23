import { Outlet } from "react-router-dom";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleOperationsDiverses = () => {
  const { user } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.HISTORIQUE_OPERATIONS,
      label: "Historique des opérations",
      aLeDroit: hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E),
    },
    {
      path: URLS.RESULTATS_EXECUTION,
      label: "Résultats d'exécutions",
      aLeDroit: hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E),
    },
  ];

  return (
    <SquelettePage fluid={false} navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleOperationsDiverses;
