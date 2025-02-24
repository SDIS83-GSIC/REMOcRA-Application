import { Outlet } from "react-router-dom";
import { hasDroit } from "../../droits.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";
import { URLS } from "../../routes.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import Header, { NavToProps } from "../Header/Header.tsx";

const ModuleOldeb = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const navLinks: NavToProps[] = [
    {
      path: URLS.OLDEB_ACCES_RAPIDE,
      label: "Accès rapide",
      aLeDroit:
        hasDroit(user, TYPE_DROIT.OLDEB_C) ||
        hasDroit(user, TYPE_DROIT.OLDEB_D) ||
        hasDroit(user, TYPE_DROIT.OLDEB_R) ||
        hasDroit(user, TYPE_DROIT.OLDEB_U),
    },
    {
      path: URLS.OLDEB_LOCALISATION,
      label: "Localisation",
      aLeDroit:
        hasDroit(user, TYPE_DROIT.OLDEB_C) ||
        hasDroit(user, TYPE_DROIT.OLDEB_D) ||
        hasDroit(user, TYPE_DROIT.OLDEB_R) ||
        hasDroit(user, TYPE_DROIT.OLDEB_U),
    },
    {
      path: URLS.OLDEB_LIST,
      label: "Obligations légales",
      aLeDroit:
        hasDroit(user, TYPE_DROIT.OLDEB_C) ||
        hasDroit(user, TYPE_DROIT.OLDEB_D) ||
        hasDroit(user, TYPE_DROIT.OLDEB_R) ||
        hasDroit(user, TYPE_DROIT.OLDEB_U),
    },
    {
      path: URLS.OLDEB_PROPRIETAIRE_LIST,
      label: "Propriétaires",
      aLeDroit:
        hasDroit(user, TYPE_DROIT.OLDEB_C) ||
        hasDroit(user, TYPE_DROIT.OLDEB_D) ||
        hasDroit(user, TYPE_DROIT.OLDEB_R) ||
        hasDroit(user, TYPE_DROIT.OLDEB_U),
    },
  ];

  return (
    <SquelettePage navbar={<Header links={navLinks} />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleOldeb;
