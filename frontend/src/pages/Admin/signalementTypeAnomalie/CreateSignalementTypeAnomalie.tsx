import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateSignalementTypeAnomalie = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ANOMALIE}
      redirectLink={URLS.LIST_SIGNALEMENT_TYPE_ANOMALIE}
      titrePage="Signalement - Création d'un type d'anomalie"
    />
  );
};

export default CreateSignalementTypeAnomalie;
