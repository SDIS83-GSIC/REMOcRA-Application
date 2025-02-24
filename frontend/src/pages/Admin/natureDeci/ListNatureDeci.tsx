import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListNatureDeci = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Natures DECI"
        addButtonTitle={"Ajouter une nature DECI"}
        pageIcon={<IconPei />}
        lienPageAjout={URLS.ADD_NATURE_DECI}
        typeNomenclature={NOMENCLATURE.NATURE_DECI}
        lienPageUpdate={URLS.UPDATE_NATURE_DECI}
      />
    </>
  );
};

export default ListNatureDeci;
