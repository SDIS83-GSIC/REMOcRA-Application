import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
const ListFonctionContact = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Fonction de contact"
        addButtonTitle={"Ajouter une fonction de contact"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_FONCTION_CONTACT}
        typeNomenclature={NOMENCLATURE.FONCTION_CONTACT}
        lienPageUpdate={URLS.UPDATE_FONCTION_CONTACT}
      />
    </>
  );
};
export default ListFonctionContact;
