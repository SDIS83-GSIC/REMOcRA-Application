import { Container } from "react-bootstrap";
import CreateButton from "../../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../../components/Form/SelectEnumOption.tsx";
import { IconCrise } from "../../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../../components/Table/QueryTable.tsx";
import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";
import url from "../../../../module/fetch.tsx";
import filterValuesTypeCriseCategorie from "./FilterTypeCriseCategorie.tsx";

const ListTypeCriseCategorie = () => {
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
