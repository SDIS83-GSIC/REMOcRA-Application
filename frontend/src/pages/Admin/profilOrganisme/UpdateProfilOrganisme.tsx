import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateProfilOrganisme = () => {
  const { profilOrganismeId } = useParams();

  return (
    profilOrganismeId && (
      <UpdateNomenclature
        nomenclatureId={profilOrganismeId}
        typeNomenclature={NOMENCLATURE.PROFIL_ORGANISME}
        redirectLink={URLS.LIST_PROFIL_ORGANSIME}
      />
    )
  );
};

export default UpdateProfilOrganisme;
