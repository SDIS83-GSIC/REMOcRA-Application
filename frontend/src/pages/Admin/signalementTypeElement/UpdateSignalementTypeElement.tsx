import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateSignalementTypeElement = () => {
  const { signalementTypeElementId } = useParams();

  return (
    signalementTypeElementId && (
      <UpdateNomenclature
        nomenclatureId={signalementTypeElementId}
        typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ELEMENT}
        redirectLink={URLS.LIST_SIGNALEMENT_TYPE_ELEMENT}
        titrePage="Signalement - Modification d'un type d'element"
      />
    )
  );
};

export default UpdateSignalementTypeElement;
