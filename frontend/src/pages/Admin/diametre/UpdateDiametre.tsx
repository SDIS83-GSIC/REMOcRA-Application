import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateDiametre = () => {
  const { diametreId } = useParams();

  return (
    diametreId && (
      <UpdateNomenclature
        nomenclatureId={diametreId}
        typeNomenclature={NOMENCLATURE.DIAMETRE}
        redirectLink={URLS.LIST_DIAMETRE}
      />
    )
  );
};

export default UpdateDiametre;
