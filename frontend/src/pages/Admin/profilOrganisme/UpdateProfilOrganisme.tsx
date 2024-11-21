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
        redirectLink={URLS.LIST_PROFIL_ORGANISME}
        titrePage="Modification d'un profil d'organisme"
        isFkRequired={true}
      />
    )
  );
};

export default UpdateProfilOrganisme;
