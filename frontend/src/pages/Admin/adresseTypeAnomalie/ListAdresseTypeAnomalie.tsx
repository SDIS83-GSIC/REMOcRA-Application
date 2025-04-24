import { IconAnomalie } from "../../../components/Icon/Icon.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import { URLS } from "../../../routes.tsx";

const ListAdresseTypeAnomalie = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Adresse - Type anomalie"
        addButtonTitle={"Ajouter un type d'anomalie"}
        pageIcon={<IconAnomalie />}
        lienPageAjout={URLS.ADD_ADRESSE_TYPE_ANOMALIE}
        typeNomenclature={NOMENCLATURE.ADRESSE_TYPE_ANOMALIE}
        lienPageUpdate={URLS.UPDATE_ADRESSE_TYPE_ANOMALIE}
        hasProtectedValue={false}
      />
    </>
  );
};

export default ListAdresseTypeAnomalie;
