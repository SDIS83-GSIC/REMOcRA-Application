import { IconCrise, IconList } from "../../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../../components/NomenclatureComponent/ListNomenclature.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../../components/Table/TableActionColumn.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

type TypeCriseRowType = {
  value: string;
  original: {
    typeCriseId: string;
    typeCriseCode: string;
    typeCriseLibelle: string;
    typeCriseActif: boolean;
  };
};

const ListTypeCrise = () => {
  const additionalActionButton: ButtonType = {
    row: (row: TypeCriseRowType) => {
      return row;
    },
    textEnable: "Accéder aux catégories d'évènement associées",
    route: () => URLS.LIST_EVENEMENT_CATEGORIE,
    type: TYPE_BUTTON.LINK,
    icon: <IconList />,
    search: (obj: TypeCriseRowType) => {
      return new URLSearchParams({
        filterBy: JSON.stringify({ evenementCategorieTypeCriseId: obj.value }),
      }).toString();
    },
  };

  return (
    <>
      <ListNomenclature
        pageTitle="Type de crise"
        addButtonTitle={"Ajouter un type de crise"}
        pageIcon={<IconCrise />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_CRISE}
        typeNomenclature={NOMENCLATURE.TYPE_CRISE}
        lienPageUpdate={URLS.UPDATE_TYPE_CRISE}
        additionalActionButton={additionalActionButton}
      />
    </>
  );
};

export default ListTypeCrise;
