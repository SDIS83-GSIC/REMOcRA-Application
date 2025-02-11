import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import Header from "../../components/Header/Header.tsx";
import { IconExport, IconList } from "../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../components/Table/TableActionColumn.tsx";
import url from "../../module/fetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import SquelettePage from "../SquelettePage.tsx";
import FilterValues from "./FilterModuleDocument.tsx";

const ListModuleDocument = () => {
  const { moduleType, moduleId } = useParams();
  return (
    <SquelettePage navbar={<Header />}>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des " + moduleType?.toLowerCase() + "s"}
        />
        <QueryTable
          query={url`/api/modules/document/all?${{
            moduleId: moduleId,
            moduleType: moduleType,
          }}`}
          columns={[
            {
              Header: "Libellé",
              accessor: "libelle",
              sortField: "libelle",
              Filter: <FilterInput type="text" name="libelle" />,
            },
            {
              Header: "Mise à jour le",
              accessor: "date",
              sortField: "date",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && formatDateTime(value.value)}
                  </div>
                );
              },
            },
            ActionColumn({
              Header: "Actions",
              accessor: "id",
              buttons: [
                {
                  row: (row) => {
                    return row;
                  },
                  route: (id) => url`/api/document/telecharger/` + id,
                  type: TYPE_BUTTON.CUSTOM,
                  icon: <IconExport />,
                  textEnable: "Télécharger le document",
                  classEnable: "warning",
                },
              ],
            }),
          ]}
          idName={"tableModuleDocument"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </SquelettePage>
  );
};

export default ListModuleDocument;
