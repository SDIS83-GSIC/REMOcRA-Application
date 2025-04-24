import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateAdresseTypeElement = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ELEMENT}
      redirectLink={URLS.LIST_ADRESSE_TYPE_ANOMALIE}
      titrePage="Adresse - Création d'un type d'element"
    />
  );
};

export default CreateAdresseTypeElement;
