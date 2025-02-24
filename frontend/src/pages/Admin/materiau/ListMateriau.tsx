import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListMateriau = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Matériaux"
        addButtonTitle={"Ajouter un matériau"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_MATERIAU}
        typeNomenclature={NOMENCLATURE.MATERIAU}
        lienPageUpdate={URLS.UPDATE_MATERIAU}
      />
    </>
  );
};

export default ListMateriau;
