import { Container } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import CreateButton from "../../../components/Form/CreateButton.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";

const ProfilDroitList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={"Groupes de fonctionnalités"}
        right={
          <CreateButton
            href={URLS.PROFIL_DROIT_CREATE}
            title={"Ajouter un groupe"}
          />
        }
      />
      <QueryTable
        query={url`/api/profil-droit`}
        filterContext={useFilterContext({})}
        columns={[
          {
            Header: "Nom",
            accessor: "profilDroitLibelle",
            sortField: "profilDroitLibelle",
          },
          {
            Header: "Code",
            accessor: "profilDroitCode",
            sortField: "profilDroitCode",
          },
          {
            Header: "Actif",
            accessor: "profilDroitActif",
            Cell: (value) => {
              return (
                <Form.Check
                  type="checkbox"
                  disabled
                  checked={value.value === true}
                />
              );
            },
            sortField: "profilDroitActif",
          },
          ActionColumn({
            Header: "Actions",
            accessor: "profilDroitId",
            buttons: [
              {
                row: (row) => {
                  return row;
                },
                href: (profilDroitId) =>
                  URLS.PROFIL_DROIT_UPDATE(profilDroitId),
                type: TYPE_BUTTON.UPDATE,
              },
            ],
          }),
        ]}
        idName={"profil-droit"}
      />
    </Container>
  );
};

export default ProfilDroitList;
