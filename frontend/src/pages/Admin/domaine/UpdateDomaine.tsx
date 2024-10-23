import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateDomaine = () => {
  const { domaineId } = useParams();

  return (
    domaineId && (
      <UpdateNomenclature
        nomenclatureId={domaineId}
        typeNomenclature={NOMENCLATURE.DOMAINE}
        redirectLink={URLS.LIST_DOMAINE}
      />
    )
  );
};

export default UpdateDomaine;
