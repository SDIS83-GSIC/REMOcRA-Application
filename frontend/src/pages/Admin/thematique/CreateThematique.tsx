import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateThematique = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.THEMATIQUE}
      redirectLink={URLS.LIST_THEMATIQUE}
    />
  );
};

export default CreateThematique;
