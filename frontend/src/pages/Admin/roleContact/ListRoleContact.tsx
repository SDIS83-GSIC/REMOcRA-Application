import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListRoleContact = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des rôles des contacts"
        pageIcon={<IconPei />}
        lienPageAjout={URLS.ADD_ROLE_CONTACT}
        typeNomenclature={NOMENCLATURE.ROLE_CONTACT}
        lienPageUpdate={URLS.UPDATE_ROLE_CONTACT}
      />
    </>
  );
};

export default ListRoleContact;
