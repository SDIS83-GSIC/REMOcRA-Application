import { Container } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";

const GroupeFonctionnalitesList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={"Groupes de fonctionnalités"}
        right={
          <CreateButton
            href={URLS.GROUPE_FONCTIONNALITES_CREATE}
            title={"Ajouter un groupe"}
          />
        }
      />
      <QueryTable
        query={url`/api/groupe-fonctionnalites`}
        filterContext={useFilterContext({})}
        columns={[
          {
            Header: "Nom",
            accessor: "groupeFonctionnalitesLibelle",
            sortField: "groupeFonctionnalitesLibelle",
          },
          {
            Header: "Code",
            accessor: "groupeFonctionnalitesCode",
            sortField: "groupeFonctionnalitesCode",
          },
          {
            Header: "Actif",
            accessor: "groupeFonctionnalitesActif",
            Cell: (value) => {
              return (
                <Form.Check
                  type="checkbox"
                  disabled
                  checked={value.value === true}
                />
              );
            },
            sortField: "groupeFonctionnalitesActif",
          },
          ActionColumn({
            Header: "Actions",
            accessor: "groupeFonctionnalitesId",
            buttons: [
              {
                row: (row) => {
                  return row;
                },
                route: (groupeFonctionnalitesId) =>
                  URLS.GROUPE_FONCTIONNALITES_UPDATE(groupeFonctionnalitesId),
                type: TYPE_BUTTON.UPDATE,
              },
            ],
          }),
        ]}
        idName={"groupe-fonctionnalites"}
      />
    </Container>
  );
};

export default GroupeFonctionnalitesList;
