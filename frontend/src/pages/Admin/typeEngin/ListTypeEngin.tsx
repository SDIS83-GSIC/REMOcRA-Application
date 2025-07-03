import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListTypeEngin = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Types d'engin"
        addButtonTitle={"Ajouter un type d'engin"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_ENGIN}
        typeNomenclature={NOMENCLATURE.TYPE_ENGIN}
        lienPageUpdate={URLS.UPDATE_TYPE_ENGIN}
      />
    </>
  );
};

export default ListTypeEngin;
