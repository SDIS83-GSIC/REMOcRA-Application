import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListTypePenaAspiration = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des types de dispositifs d'aspiration"
        addButtonTitle={"Ajouter un type d'aspiration"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_PENA_ASPIRATION}
        typeNomenclature={NOMENCLATURE.TYPE_PENA_ASPIRATION}
        lienPageUpdate={URLS.UPDATE_TYPE_PENA_ASPIRATION}
      />
    </>
  );
};

export default ListTypePenaAspiration;
