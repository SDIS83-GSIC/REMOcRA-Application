import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateSignalementTypeAnomalie = () => {
  const { signalementTypeAnomalieId } = useParams();

  return (
    signalementTypeAnomalieId && (
      <UpdateNomenclature
        nomenclatureId={signalementTypeAnomalieId}
        typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ANOMALIE}
        redirectLink={URLS.LIST_SIGNALEMENT_TYPE_ANOMALIE}
        titrePage="Signalement - Modification d'un type d'anomalie"
      />
    )
  );
};

export default UpdateSignalementTypeAnomalie;
