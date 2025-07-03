import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateTypeEngin = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.TYPE_ENGIN}
      redirectLink={URLS.LIST_TYPE_ENGIN}
      titrePage="Création d'un type d'engin"
    />
  );
};

export default CreateTypeEngin;
