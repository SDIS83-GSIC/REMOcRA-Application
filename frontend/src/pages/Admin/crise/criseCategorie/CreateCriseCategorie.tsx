import CreateNomenclature from "../../../../components/NomenclatureComponent/CreateNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const CreateCriseCategorie = () => {
  return (
    <CreateNomenclature
      typeNomenclature={NOMENCLATURE.CRISE_CATEGORIE}
      redirectLink={URLS.LIST_CRISE_CATEGORIE}
      titrePage="Création d'une catégorie de crise"
    />
  );
};

export default CreateCriseCategorie;
