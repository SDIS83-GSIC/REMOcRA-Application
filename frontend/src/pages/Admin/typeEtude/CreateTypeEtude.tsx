import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypeEtude = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_ETUDE}
      redirectLink={URLS.LIST_TYPE_ETUDE}
    />
  );
};

export default CreateTypeEtude;
