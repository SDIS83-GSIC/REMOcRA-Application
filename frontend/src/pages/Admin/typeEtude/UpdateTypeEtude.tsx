import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypeEtude = () => {
  const { typeEtudeId } = useParams();

  return (
    typeEtudeId && (
      <UpdateNomenclature
        nomenclatureId={typeEtudeId}
        typeNomenclature={NOMENCLATURE.TYPE_ETUDE}
        redirectLink={URLS.LIST_TYPE_ETUDE}
      />
    )
  );
};

export default UpdateTypeEtude;
