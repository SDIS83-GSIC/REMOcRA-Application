import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListTypeEtude = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des types d'études"
        addButtonTitle={"Ajouter un type d'étude"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_ETUDE}
        typeNomenclature={NOMENCLATURE.TYPE_ETUDE}
        lienPageUpdate={URLS.UPDATE_TYPE_ETUDE}
      />
    </>
  );
};

export default ListTypeEtude;
