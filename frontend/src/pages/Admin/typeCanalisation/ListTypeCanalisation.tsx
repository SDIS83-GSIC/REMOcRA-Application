import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListTypeCanalisation = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des types de canalisation"
        addButtonTitle={"Ajouter un type de canalisation"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_CANALISATION}
        typeNomenclature={NOMENCLATURE.TYPE_CANALISATION}
        lienPageUpdate={URLS.UPDATE_TYPE_CANALISATION}
      />
    </>
  );
};

export default ListTypeCanalisation;
