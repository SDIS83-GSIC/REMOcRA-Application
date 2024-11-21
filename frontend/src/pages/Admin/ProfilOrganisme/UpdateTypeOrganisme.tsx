import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateTypeOrganisme = () => {
  const { typeOrganismeId } = useParams();

  return (
    typeOrganismeId && (
      <UpdateNomenclature
        nomenclatureId={typeOrganismeId}
        typeNomenclature={NOMENCLATURE.TYPE_ORGANISME}
        redirectLink={URLS.LIST_TYPE_ORGANISME}
        isFkRequired={true}
        titrePage="=Modification d'un type d'organisme"
      />
    )
  );
};

export default UpdateTypeOrganisme;
