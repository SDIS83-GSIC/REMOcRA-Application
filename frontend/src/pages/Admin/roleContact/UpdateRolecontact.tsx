import { useParams } from "react-router-dom";
import UpdateNomenclature from "../../../components/NomenclatureComponent/UpdateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const UpdateRoleContact = () => {
  const { roleContactId } = useParams();

  return (
    roleContactId && (
      <UpdateNomenclature
        nomenclatureId={roleContactId}
        typeNomenclature={NOMENCLATURE.ROLE_CONTACT}
        redirectLink={URLS.LIST_ROLE_CONTACT}
      />
    )
  );
};

export default UpdateRoleContact;
