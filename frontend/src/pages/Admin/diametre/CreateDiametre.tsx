import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateDiametre = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.DIAMETRE}
      redirectLink={URLS.LIST_DIAMETRE}
    />
  );
};

export default CreateDiametre;
