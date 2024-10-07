import { Button, Container } from "react-bootstrap";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import TypePeiEnum from "../../../enums/TypePeiEnum.tsx";
import EditColumn, {
  BooleanColumn,
  DeleteColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import filterValuesNature from "./FilterNature.tsx";

const ListNature = () => {
  return (
    <>
      <PageTitle title="Liste des natures" icon={<IconPei />} />
      <Container>
        <Button
          type="button"
          variant="primary"
          href={URLS.ADD_NATURE}
          className="mb-1"
        >
          Ajouter une nature
        </Button>
        <QueryTable
          query={url`/api/nature/get`}
          filterValuesToVariable={filterValuesNature}
          filterContext={useFilterContext({
            natureActif: undefined,
            natureCode: undefined,
            natureLibelle: undefined,
            natureTypePei: undefined,
            natureProtected: undefined,
          })}
          idName={"ListNature"}
          columns={[
            {
              Header: "Libellé",
              accessor: "natureLibelle",
              sortField: "natureLibelle",
              Filter: <FilterInput type="text" name="natureLibelle" />,
            },
            {
              Header: "Type",
              accessor: "natureTypePei",
              sortField: "natureTypePei",
              Filter: (
                <SelectEnumOption
                  options={TypePeiEnum}
                  name={"natureTypePei"}
                />
              ),
            },
            {
              Header: "Code",
              accessor: "natureCode",
              sortField: "natureCode",
              Filter: <FilterInput type="text" name="natureCode" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "natureActif",
              sortField: "natureActif",
              Filter: (
                <SelectEnumOption name="natureActif" options={VRAI_FAUX} />
              ),
            }),
            ProtectedColumn({
              Header: "Protégé",
              accessor: "natureProtected",
              sortField: "natureProtected",
              Filter: (
                <SelectEnumOption name="natureProtected" options={VRAI_FAUX} />
              ),
            }),
            EditColumn({
              to: (data) => URLS.UPDATE_NATURE(data),
              accessor: "natureId",
              title: true,
              canEditFunction() {
                return true; // TODO prendre en compte les droits
              },
              disable: (v) => {
                return v.original.natureProtected;
              },
            }),
            DeleteColumn({
              path: url`/api/nature/delete/`,
              title: true,
              canSupress: true,
              accessor: "natureId",
              disable: (v) => {
                return v.original.natureProtected;
              }, // TODO prendre en compte les droits
            }),
          ]}
        />
        |
      </Container>
    </>
  );
};
export default ListNature;
