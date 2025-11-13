import { IconCrise } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import ListNomenclature from "../../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";

const ListEvenementCategorie = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Catégorie d'évènement"
        addButtonTitle={"Ajouter une catégorie d'évènement"}
        pageIcon={<IconCrise />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_EVENEMENT_CATEGORIE}
        typeNomenclature={NOMENCLATURE.EVENEMENT_CATEGORIE}
        lienPageUpdate={URLS.UPDATE_EVENEMENT_CATEGORIE}
      />
    </>
  );
};

export default ListEvenementCategorie;
