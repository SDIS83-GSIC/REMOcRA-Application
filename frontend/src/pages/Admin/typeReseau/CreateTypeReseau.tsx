import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypeReseau = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_RESEAU}
      redirectLink={URLS.LIST_TYPE_RESEAU}
    />
  );
};

export default CreateTypeReseau;
