import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateNatureDeci = () => {
  const { natureDeciId } = useParams();

  return (
    natureDeciId && (
      <UpdateNomenclature
        nomenclatureId={natureDeciId}
        typeNomenclature={NOMENCLATURE.NATURE_DECI}
        redirectLink={URLS.LIST_NATURE_DECI}
      />
    )
  );
};

export default UpdateNatureDeci;
