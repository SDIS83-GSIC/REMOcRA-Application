import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateAdresseTypeAnomalie = () => {
  const { adresseTypeAnomalieId } = useParams();

  return (
    adresseTypeAnomalieId && (
      <UpdateNomenclature
        nomenclatureId={adresseTypeAnomalieId}
        typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ANOMALIE}
        redirectLink={URLS.LIST_ADRESSE_TYPE_ANOMALIE}
        titrePage="Adresse - Modification d'un type d'anomalie"
      />
    )
  );
};

export default UpdateAdresseTypeAnomalie;
