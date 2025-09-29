import { Container } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

const GroupeFonctionnalitesList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={
          <>
            Groupes de fonctionnalités
            <TooltipCustom
              tooltipText={
                "Un groupe de fonctionnalités permet de regrouper des droits d'accès afin de les attribuer à une catégorie d'utilisateurs"
              }
              tooltipId={"tooltip-groupe-fonctionnalites"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
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
          BooleanColumn({
            Header: "Actif",
            accessor: "groupeFonctionnalitesActif",
            sortField: "groupeFonctionnalitesActif",
          }),
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
