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
import filterValuesTypeCriseCategorie from "./FilterTypeCriseCategorie.tsx";

const ListTypeCriseCategorie = () => {
  const { user } = useAppContext();
  const colonne = [
    {
      Header: "Code",
      accessor: "typeCriseCategorieCode",
      sortField: "typeCriseCategorieCode",
      Filter: <FilterInput type="text" name="typeCriseCategorieCode" />,
    },
    {
      Header: "Libellé",
      accessor: "typeCriseCategorieLibelle",
      sortField: "typeCriseCategorieLibelle",
      Filter: <FilterInput type="text" name="typeCriseCategorieLibelle" />,
    },
    {
      Header: "Type de géométrie",
      accessor: "typeCriseCategorieTypeGeometrie",
      sortField: "typeCriseCategorieTypeGeometrie",
      Filter: (
        <SelectEnumOption
          options={TYPE_GEOMETRIE}
          name={"typeCriseCategorieTypeGeometrie"}
        />
      ),
    },
    {
      Header: "Catégorie de la crise",
      accessor: "criseCategorieLibelle",
      sortField: "criseCategorieLibelle",
      Filter: <FilterInput type="text" name="criseCategorieLibelle" />,
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
      pathname: url`/api/type-crise-categorie/delete/`,
    });
  }

  colonne.push(
    ActionColumn({
      Header: "Actions",
      accessor: "typeCriseCategorieId",
      buttons: listeButton,
    }),
  );

  return (
    <>
      <Container>
        <PageTitle
          title="Liste des types de catégories de crise"
          icon={<IconCrise />}
          right={
            <CreateButton
              title="Ajouter un type de catégorie de crise"
              href={URLS.ADD_TYPE_CRISE_CATEGORIE}
            />
          }
        />
        <QueryTable
          query={url`/api/type-crise-categorie`}
          filterValuesToVariable={filterValuesTypeCriseCategorie}
          filterContext={useFilterContext({})}
          idName={"ListTypeCriseCategorie"}
          columns={colonne}
        />
      </Container>
    </>
  );
};
export default ListTypeCriseCategorie;
