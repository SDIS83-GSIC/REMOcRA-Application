import { IconAnomalie } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";

const ListAnomalieCategorie = () => {
  return (
    <>
      <ListNomenclature
        pageTitle="Catégories d'anomalies"
        addButtonTitle={"Ajouter une catégorie d'anomalie"}
        pageIcon={<IconAnomalie />}
        hasProtectedValue={false}
        lienPageAjout={URLS.ADD_ANOMALIE_CATEGORIE}
        typeNomenclature={NOMENCLATURE.ANOMALIE_CATEGORIE}
        lienPageUpdate={URLS.UPDATE_ANOMALIE_CATEGORIE}
      />
    </>
  );
};

export default ListAnomalieCategorie;
