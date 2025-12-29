import { IconCrise, IconList } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import ListNomenclature from "../../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../../components/Table/TableActionColumn.tsx";

const ListEvenementCategorie = () => {
  const additionalActionButton: ButtonType = {
    row: (row: any) => {
      return row;
    },
    textEnable: "Accéder aux sous catégories d'évènement",
    route: () => URLS.LIST_EVENEMENT_SOUS_CATEGORIE,
    type: TYPE_BUTTON.LINK,
    icon: <IconList />,
    search: (obj: any) => {
      return new URLSearchParams({
        filterBy: JSON.stringify({ evenementCategorieId: obj.value }),
      }).toString();
    },
  };

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
        additionalActionButton={additionalActionButton}
      />
    </>
  );
};

export default ListEvenementCategorie;
