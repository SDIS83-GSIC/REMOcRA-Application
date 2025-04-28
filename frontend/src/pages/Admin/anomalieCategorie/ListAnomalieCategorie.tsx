import { IconAnomalie, IconSortList } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ListNomenclature from "../../../components/NomenclatureComponent/ListNomenclature.tsx";
import NOMENCLATURE from "../../../enums/NomenclaturesEnum.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
const ListAnomalieCategorie = () => {
  const sortButton = (
    <CustomLinkButton
      pathname={URLS.SORT_ANOMALIE_CATEGORIE}
      variant={"primary"}
    >
      <IconSortList /> Changer l&apos;ordre des éléments
    </CustomLinkButton>
  );

  return (
    <ListNomenclature
      pageTitle="Catégories d'anomalies"
      addButtonTitle={"Ajouter une catégorie d'anomalie"}
      pageIcon={<IconAnomalie />}
      lienPageAjout={URLS.ADD_ANOMALIE_CATEGORIE}
      typeNomenclature={NOMENCLATURE.ANOMALIE_CATEGORIE}
      lienPageUpdate={URLS.UPDATE_ANOMALIE_CATEGORIE}
      additionalButton={sortButton}
    />
  );
};

export default ListAnomalieCategorie;
