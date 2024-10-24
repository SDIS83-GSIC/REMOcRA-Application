import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypePenaAspiration = () => {
  const { typePenaAspirationId } = useParams();

  return (
    typePenaAspirationId && (
      <UpdateNomenclature
        nomenclatureId={typePenaAspirationId}
        typeNomenclature={NOMENCLATURE.TYPE_PENA_ASPIRATION}
        redirectLink={URLS.LIST_TYPE_PENA_ASPIRATION}
      />
    )
  );
};

export default UpdateTypePenaAspiration;
