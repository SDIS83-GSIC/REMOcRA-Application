import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypePenaAspiration = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_PENA_ASPIRATION}
      redirectLink={URLS.LIST_TYPE_PENA_ASPIRATION}
    />
  );
};

export default CreateTypePenaAspiration;
