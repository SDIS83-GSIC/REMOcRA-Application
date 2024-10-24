import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypeCanalisation = () => {
  const { typeCanalisationId } = useParams();

  return (
    typeCanalisationId && (
      <UpdateNomenclature
        nomenclatureId={typeCanalisationId}
        typeNomenclature={NOMENCLATURE.TYPE_CANALISATION}
        redirectLink={URLS.LIST_TYPE_CANALISATION}
      />
    )
  );
};

export default UpdateTypeCanalisation;
