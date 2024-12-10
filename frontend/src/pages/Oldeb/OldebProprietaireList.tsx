import React from "react";
import { useNavigate } from "react-router-dom";
import { Container } from "react-bootstrap";
import { URLS } from "../../routes.tsx";
import url from "../../module/fetch.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import {
  IconDelete,
  IconEdit,
  IconProprietaire,
} from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import { TYPE_BUTTON } from "../../components/Table/TableActionColumn.tsx";
import CreateButton from "../../components/Form/CreateButton.tsx";
import filterValuesToVariable from "./OldebProprietaireFilter.tsx";

const OldebProprietaireList = () => {
  const navigate = useNavigate();
  return (
    <Container>
      <PageTitle
        icon={<IconProprietaire />}
        title={"Liste des propriétaires"}
        right={
          <CreateButton
            onClick={() => navigate(URLS.OLDEB_PROPRIETAIRE_CREATE)}
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
                href: (oldebProprietaireId) =>
                  URLS.OLDEB_PROPRIETAIRE_UPDATE(oldebProprietaireId),
                type: TYPE_BUTTON.UPDATE,
                icon: <IconEdit />,
              },
              {
                row: (row) => {
                  return row;
                },
                path: url`/api/proprietaire/`,
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
