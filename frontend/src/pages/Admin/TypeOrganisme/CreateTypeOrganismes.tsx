import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypeOrganisme = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_ORGANISME}
      redirectLink={URLS.LIST_TYPE_ORGANISME}
      titrePage="Création d'un type d'organisme"
    />
  );
};

export default CreateTypeOrganisme;
