import { IconSignalement } from "../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const ListSignalementTypeElement = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Signalement - Type élément"
        addButtonTitle={"Ajouter un type d'élément"}
        pageIcon={<IconSignalement />}
        lienPageAjout={URLS.ADD_SIGNALEMENT_TYPE_ELEMENT}
        typeNomenclature={NOMENCLATURE.SIGNALEMENT_TYPE_ELEMENT}
        lienPageUpdate={URLS.UPDATE_SIGNALEMENT_TYPE_ELEMENT}
        hasProtectedValue={false}
      />
    </>
  );
};

export default ListSignalementTypeElement;
