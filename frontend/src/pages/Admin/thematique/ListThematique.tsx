import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListThematique = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Thématiques"
        addButtonTitle={"Ajouter une thématique"}
        pageIcon={<IconPei />}
        lienPageAjout={URLS.ADD_THEMATIQUE}
        typeNomenclature={NOMENCLATURE.THEMATIQUE}
        lienPageUpdate={URLS.UPDATE_THEMATIQUE}
      />
    </>
  );
};

export default ListThematique;
