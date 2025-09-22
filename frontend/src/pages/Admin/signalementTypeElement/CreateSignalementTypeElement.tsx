import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateSignalementTypeElement = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ELEMENT}
      redirectLink={URLS.LIST_SIGNALEMENT_TYPE_ANOMALIE}
      titrePage="Signalement - Création d'un type d'element"
    />
  );
};

export default CreateSignalementTypeElement;
