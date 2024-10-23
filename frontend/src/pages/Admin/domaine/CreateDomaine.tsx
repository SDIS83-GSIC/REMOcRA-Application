import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateDomaine = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.DOMAINE}
      redirectLink={URLS.LIST_DOMAINE}
    />
  );
};

export default CreateDomaine;
