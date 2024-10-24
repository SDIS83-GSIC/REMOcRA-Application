import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateModelePibi = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.MODELE_PIBI}
      redirectLink={URLS.LIST_MODELE_PIBI}
    />
  );
};

export default CreateModelePibi;
