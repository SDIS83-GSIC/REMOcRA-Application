import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListMarquePibi = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Marques des PIBI"
        addButtonTitle={"Ajouter une marque"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_MARQUE_PIBI}
        typeNomenclature={NOMENCLATURE.MARQUE_PIBI}
        lienPageUpdate={URLS.UPDATE_MARQUE_PIBI}
      />
    </>
  );
};

export default ListMarquePibi;
