import CreateNomenclature from "../../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const CreateTypeCrise = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_CRISE}
      redirectLink={URLS.LIST_TYPE_CRISE}
      titrePage="Création d'un type de crise"
    />
  );
};

export default CreateTypeCrise;
