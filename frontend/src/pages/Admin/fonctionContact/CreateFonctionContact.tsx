import CreateNomenclature from "../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";
const CreateFonctionContact = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.FONCTION_CONTACT}
      redirectLink={URLS.LIST_FONCTION_CONTACT}
      titrePage="Création d'une fonction de contact"
    />
  );
};
export default CreateFonctionContact;
