import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateNatureDeci = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.NATURE_DECI}
      redirectLink={URLS.LIST_NATURE_DECI}
    />
  );
};

export default CreateNatureDeci;
