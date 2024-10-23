import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateMarquePibi = () => {
  const { marquePibiId } = useParams();

  return (
    marquePibiId && (
      <UpdateNomenclature
        nomenclatureId={marquePibiId}
        typeNomenclature={NOMENCLATURE.MARQUE_PIBI}
        redirectLink={URLS.LIST_MARQUE_PIBI}
      />
    )
  );
};

export default UpdateMarquePibi;
