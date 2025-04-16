import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateAnomalieCategorie = () => {
  const { anomalieCategorieId } = useParams();

  return (
    anomalieCategorieId && (
      <UpdateNomenclature
        nomenclatureId={anomalieCategorieId}
        typeNomenclature={NOMENCLATURE.ANOMALIE_CATEGORIE}
        redirectLink={URLS.LIST_ANOMALIE_CATEGORIE}
      />
    )
  );
};

export default UpdateAnomalieCategorie;
