import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateProfilOrganisme = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.PROFIL_ORGANISME}
      redirectLink={URLS.LIST_PROFIL_ORGANISME}
      titrePage="Création d'un profil d'organisme"
      isFkRequired={true}
    />
  );
};

export default CreateProfilOrganisme;
