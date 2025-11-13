import CreateNomenclature from "../../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const CreateEvenementCategorie = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.EVENEMENT_CATEGORIE}
      redirectLink={URLS.LIST_EVENEMENT_CATEGORIE}
      titrePage="Création d'une catégorie d'évènement"
    />
  );
};

export default CreateEvenementCategorie;
