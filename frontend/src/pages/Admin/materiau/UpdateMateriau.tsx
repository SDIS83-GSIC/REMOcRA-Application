import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateMateriau = () => {
  const { materiauId } = useParams();

  return (
    materiauId && (
      <UpdateNomenclature
        nomenclatureId={materiauId}
        typeNomenclature={NOMENCLATURE.MATERIAU}
        redirectLink={URLS.LIST_MATERIAU}
      />
    )
  );
};

export default UpdateMateriau;
