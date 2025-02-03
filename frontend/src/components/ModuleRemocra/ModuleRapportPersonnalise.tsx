import { Outlet } from "react-router-dom";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";

const ModuleRapportPersonnalise = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.EXECUTER_RAPPORT_PERSONNALISE,
      label: "Exécuter un rapport personnalisé",
      aLeDroit: hasDroit(user, TYPE_DROIT.RAPPORT_PERSONNALISE_E),
    },
  ];

  return (
    <SquelettePage fluid navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleRapportPersonnalise;
