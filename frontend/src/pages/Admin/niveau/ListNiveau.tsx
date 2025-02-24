import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListNiveau = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Niveaux"
        addButtonTitle={"Ajouter un niveau"}
        pageIcon={<IconPei />}
        lienPageAjout={URLS.ADD_NIVEAU}
        typeNomenclature={NOMENCLATURE.NIVEAU}
        lienPageUpdate={URLS.UPDATE_NIVEAU}
      />
    </>
  );
};

export default ListNiveau;
