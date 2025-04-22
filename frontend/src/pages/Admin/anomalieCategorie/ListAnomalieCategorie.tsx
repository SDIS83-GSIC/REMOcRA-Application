import { IconAnomalie, IconList } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import LinkButton from "../../../components/Button/LinkButton.tsx";

const ListAnomalieCategorie = () => {
  const sortButton = (
    <LinkButton pathname={URLS.SORT_ANOMALIE_CATEGORIE}>
      <IconList /> Changer l&apos;ordre des éléments
    </LinkButton>
  );

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
        additionalButton={sortButton}
      />
    </>
  );
};

export default ListAnomalieCategorie;
