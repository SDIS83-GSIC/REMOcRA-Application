import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateNiveau = () => {
  const { niveauId } = useParams();

  return (
    niveauId && (
      <UpdateNomenclature
        nomenclatureId={niveauId}
        typeNomenclature={NOMENCLATURE.NIVEAU}
        redirectLink={URLS.LIST_NIVEAU}
      />
    )
  );
};

export default UpdateNiveau;
