import { Container } from "react-bootstrap";
import CreateButton from "../../components/Button/CreateButton.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import {
  IconDelete,
  IconEdit,
  IconProprietaire,
} from "../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../components/Table/TableActionColumn.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import filterValuesToVariable from "./OldebProprietaireFilter.tsx";

const OldebProprietaireList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconProprietaire />}
        title={"Liste des propriétaires"}
        right={
          <CreateButton
            href={URLS.OLDEB_PROPRIETAIRE_CREATE}
            title={"Créer un propriétaire"}
          />
        }
      />
      <QueryTable
        query={url`/api/proprietaire`}
        columns={[
          {
            Header: "Nom",
            accessor: "oldebProprietaireNom",
            sortField: "oldebProprietaireNom",
            Filter: <FilterInput type="text" name="oldebProprietaireNom" />,
          },
          {
            Header: "Prenom",
            accessor: "oldebProprietairePrenom",
            sortField: "oldebProprietairePrenom",
            Filter: <FilterInput type="text" name="oldebProprietairePrenom" />,
          },
          {
            Header: "Ville",
            accessor: "oldebProprietaireVille",
            sortField: "oldebProprietaireVille",
            Filter: <FilterInput type="text" name="oldebProprietaireVille" />,
          },
          ActionColumn({
            Header: "Actions",
            accessor: "oldebProprietaireId",
            buttons: [
              {
                row: (row) => {
                  return row;
                },
                route: (oldebProprietaireId) =>
                  URLS.OLDEB_PROPRIETAIRE_UPDATE(oldebProprietaireId),
                type: TYPE_BUTTON.UPDATE,
                icon: <IconEdit />,
              },
              {
                row: (row) => {
                  return row;
                },
                pathname: url`/api/proprietaire/`,
                type: TYPE_BUTTON.DELETE,
                icon: <IconDelete />,
              },
            ],
          }),
        ]}
        idName={"oldebProprietaireId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({})}
      />
    </Container>
  );
};

export default OldebProprietaireList;
