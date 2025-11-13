import { Container } from "react-bootstrap";
import CreateButton from "../../../../components/Button/CreateButton.tsx";
import { useAppContext } from "../../../../components/App/AppProvider.tsx";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../../components/Form/SelectEnumOption.tsx";
import { IconCrise } from "../../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../../droits.tsx";
import TYPE_DROIT from "../../../../enums/DroitEnum.tsx";
import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import filterValuesEvenementSousCategorie from "./FilterTypeCriseCategorie.tsx";

const ListEvenementSousCategorie = () => {
  const { user } = useAppContext();
  const colonne = [
    {
      Header: "Code",
      accessor: "evenementSousCategorieCode",
      sortField: "evenementSousCategorieCode",
      Filter: <FilterInput type="text" name="evenementSousCategorieCode" />,
    },
    {
      Header: "Libellé",
      accessor: "evenementSousCategorieLibelle",
      sortField: "evenementSousCategorieLibelle",
      Filter: <FilterInput type="text" name="evenementSousCategorieLibelle" />,
    },
    {
      Header: "Type de géométrie",
      accessor: "evenementSousCategorieTypeGeometrie",
      sortField: "evenementSousCategorieTypeGeometrie",
      Filter: (
        <SelectEnumOption
          options={TYPE_GEOMETRIE}
          name={"evenementSousCategorieTypeGeometrie"}
        />
      ),
    },
    {
      Header: "Catégorie de l'évènement",
      accessor: "evenementCategorieLibelle",
      sortField: "evenementCategorieLibelle",
      Filter: <FilterInput type="text" name="evenementCategorieLibelle" />,
    },
  ];

  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.UPDATE,
      route: (data) => URLS.UPDATE_TYPE_CRISE_CATEGORIE(data),
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/evenement-sous-categorie/delete/`,
    });
  }

  colonne.push(
    ActionColumn({
      Header: "Actions",
      accessor: "evenementSousCategorieId",
      buttons: listeButton,
    }),
  );

  return (
    <>
      <Container>
        <PageTitle
          title="Liste des types de catégories d'évènements"
          icon={<IconCrise />}
          right={
            <CreateButton
              title="Ajouter un type de catégorie d'évènement"
              href={URLS.ADD_EVENEMENT_SOUS_CATEGORIE}
            />
          }
        />
        <QueryTable
          query={url`/api/evenement-sous-categorie`}
          filterValuesToVariable={filterValuesEvenementSousCategorie}
          filterContext={useFilterContext({})}
          idName={"ListTypeEvenementCategorie"}
          columns={colonne}
        />
      </Container>
    </>
  );
};
export default ListEvenementSousCategorie;
