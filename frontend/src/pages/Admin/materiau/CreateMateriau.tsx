import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateMateriau = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.MATERIAU}
      redirectLink={URLS.LIST_MATERIAU}
    />
  );
};

export default CreateMateriau;
