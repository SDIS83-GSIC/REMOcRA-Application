import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListDiametre = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Liste des diamètres"
        pageIcon={<IconPei />}
        lienPageAjout={URLS.ADD_DIAMETRE}
        typeNomenclature={NOMENCLATURE.DIAMETRE}
        lienPageUpdate={URLS.UPDATE_DIAMETRE}
      />
    </>
  );
};

export default ListDiametre;
