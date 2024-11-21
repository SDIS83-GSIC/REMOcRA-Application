import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateProfilUtilisateur = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.PROFIL_UTILISATEUR}
      redirectLink={URLS.LIST_PROFIL_UTILISATEUR}
      titrePage="Création d'un profil utilisateur"
    />
  );
};

export default CreateProfilUtilisateur;
