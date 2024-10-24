import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateNiveau = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.NIVEAU}
      redirectLink={URLS.LIST_NIVEAU}
    />
  );
};

export default CreateNiveau;
