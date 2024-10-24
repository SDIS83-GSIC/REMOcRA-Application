import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateModelePibi = () => {
  const { modelePibiId } = useParams();

  return (
    modelePibiId && (
      <UpdateNomenclature
        nomenclatureId={modelePibiId}
        typeNomenclature={NOMENCLATURE.MODELE_PIBI}
        redirectLink={URLS.LIST_MODELE_PIBI}
      />
    )
  );
};

export default UpdateModelePibi;
