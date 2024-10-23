import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateDiametre = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.DIAMETRE}
      redirectLink={URLS.DIAMETRE}
    />
  );
};

export default CreateDiametre;
