import { IconSignalement } from "../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const ListSignalementTypeAnomalie = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Signalement - Type anomalie"
        addButtonTitle={"Ajouter un type d'anomalie"}
        pageIcon={<IconSignalement />}
        lienPageAjout={URLS.ADD_SIGNALEMENT_TYPE_ANOMALIE}
        typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ANOMALIE}
        lienPageUpdate={URLS.UPDATE_SIGNALEMENT_TYPE_ANOMALIE}
        hasProtectedValue={false}
      />
    </>
  );
};

export default ListSignalementTypeAnomalie;
