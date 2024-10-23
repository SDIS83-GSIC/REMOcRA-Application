import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateMarquePibi = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.MARQUE_PIBI}
      redirectLink={URLS.LIST_MARQUE_PIBI}
    />
  );
};

export default CreateMarquePibi;
