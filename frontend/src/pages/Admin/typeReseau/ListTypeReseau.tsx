import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListTypeReseau = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Types de réseaux"
        addButtonTitle={"Ajouter un type de réseau"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_TYPE_RESEAU}
        typeNomenclature={NOMENCLATURE.TYPE_RESEAU}
        lienPageUpdate={URLS.UPDATE_TYPE_RESEAU}
      />
    </>
  );
};

export default ListTypeReseau;
