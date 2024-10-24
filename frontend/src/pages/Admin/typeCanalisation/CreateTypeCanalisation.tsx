import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypeCanalisation = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_CANALISATION}
      redirectLink={URLS.LIST_TYPE_CANALISATION}
    />
  );
};

export default CreateTypeCanalisation;
