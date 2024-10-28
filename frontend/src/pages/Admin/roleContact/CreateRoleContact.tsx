import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const CreateRoleContact = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.ROLE_CONTACT}
      redirectLink={URLS.LIST_ROLE_CONTACT}
    />
  );
};

export default CreateRoleContact;
