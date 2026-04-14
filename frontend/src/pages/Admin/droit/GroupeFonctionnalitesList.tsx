import { Container } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";

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
        columns={[
          {
            Header: "Nom",
            accessor: "groupeFonctionnalitesLibelle",
            sortField: "groupeFonctionnalitesLibelle",
            Filter: (
              <FilterInput type="text" name="groupeFonctionnalitesLibelle" />
            ),
          },
          {
            Header: "Code",
            accessor: "groupeFonctionnalitesCode",
            sortField: "groupeFonctionnalitesCode",
            Filter: (
              <FilterInput type="text" name="groupeFonctionnalitesCode" />
            ),
          },
          BooleanColumn({
            Header: "Actif",
            accessor: "groupeFonctionnalitesActif",
            sortField: "groupeFonctionnalitesActif",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"groupeFonctionnalitesActif"}
              />
            ),
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
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          groupeFonctionnalitesLibelle: undefined,
          groupeFonctionnalitesCode: undefined,
          groupeFonctionnalitesActif: undefined,
        })}
      />
    </Container>
  );
};

export default GroupeFonctionnalitesList;

type FilterType = {
  groupeFonctionnalitesLibelle?: string;
  groupeFonctionnalitesCode?: string;
  groupeFonctionnalitesActif?: string;
};

const filterValuesToVariable = ({
  groupeFonctionnalitesLibelle,
  groupeFonctionnalitesCode,
  groupeFonctionnalitesActif,
}: FilterType) => {
  const filter: FilterType = {};

  filterProperty(
    filter,
    groupeFonctionnalitesLibelle,
    "groupeFonctionnalitesLibelle",
  );
  filterProperty(
    filter,
    groupeFonctionnalitesCode,
    "groupeFonctionnalitesCode",
  );
  filterProperty(
    filter,
    groupeFonctionnalitesActif,
    "groupeFonctionnalitesActif",
  );

  return filter;
};

function filterProperty(
  filter: FilterType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}
