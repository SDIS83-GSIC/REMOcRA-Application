import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateThematique = () => {
  const { thematiqueId } = useParams();

  return (
    thematiqueId && (
      <UpdateNomenclature
        nomenclatureId={thematiqueId}
        typeNomenclature={NOMENCLATURE.THEMATIQUE}
        redirectLink={URLS.LIST_THEMATIQUE}
      />
    )
  );
};

export default UpdateThematique;
