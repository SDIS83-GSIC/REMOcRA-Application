import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateAnomalieCategorie = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.ANOMALIE_CATEGORIE}
      redirectLink={URLS.LIST_ANOMALIE_CATEGORIE}
    />
  );
};

export default CreateAnomalieCategorie;
