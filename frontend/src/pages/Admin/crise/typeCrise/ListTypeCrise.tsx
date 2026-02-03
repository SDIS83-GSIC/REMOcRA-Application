import { IconCrise } from "../../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../../routes.tsx";

const ListTypeCrise = () => {
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
      />
    </>
  );
};

export default ListTypeCrise;
