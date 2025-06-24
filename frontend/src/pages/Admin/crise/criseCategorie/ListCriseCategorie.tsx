import { IconCrise } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import ListNomenclature from "../../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";

const ListCriseCategorie = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Catégorie de crise"
        addButtonTitle={"Ajouter une catégorie de crise"}
        pageIcon={<IconCrise />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_CRISE_CATEGORIE}
        typeNomenclature={NOMENCLATURE.CRISE_CATEGORIE}
        lienPageUpdate={URLS.UPDATE_CRISE_CATEGORIE}
      />
    </>
  );
};

export default ListCriseCategorie;
