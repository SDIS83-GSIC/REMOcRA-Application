import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const UpdateTypeCrise = () => {
  const { typeCriseId } = useParams();

  return (
    typeCriseId && (
      <UpdateNomenclature
        nomenclatureId={typeCriseId}
        typeNomenclature={NOMENCLATURE.TYPE_CRISE}
        redirectLink={URLS.LIST_TYPE_CRISE}
        titrePage="Mise à jour d'un type de crise"
      />
    )
  );
};

export default UpdateTypeCrise;
