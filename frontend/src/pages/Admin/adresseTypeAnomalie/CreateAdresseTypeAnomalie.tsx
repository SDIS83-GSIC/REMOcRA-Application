import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateAdresseTypeAnomalie = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ANOMALIE}
      redirectLink={URLS.LIST_ADRESSE_TYPE_ANOMALIE}
      titrePage="Adresse - Création d'un type d'anomalie"
    />
  );
};

export default CreateAdresseTypeAnomalie;
