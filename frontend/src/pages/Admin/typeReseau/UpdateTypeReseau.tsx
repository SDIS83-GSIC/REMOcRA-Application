import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypeReseau = () => {
  const { typeReseauId } = useParams();

  return (
    typeReseauId && (
      <UpdateNomenclature
        nomenclatureId={typeReseauId}
        typeNomenclature={NOMENCLATURE.TYPE_RESEAU}
        redirectLink={URLS.LIST_TYPE_RESEAU}
      />
    )
  );
};

export default UpdateTypeReseau;
