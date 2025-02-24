import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListDomaine = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Domaines"
        addButtonTitle={"Ajouter un domaine"}
        pageIcon={<IconPei />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_DOMAINE}
        typeNomenclature={NOMENCLATURE.DOMAINE}
        lienPageUpdate={URLS.UPDATE_DOMAINE}
      />
    </>
  );
};

export default ListDomaine;
