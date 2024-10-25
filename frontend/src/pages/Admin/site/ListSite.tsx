import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../../Entities/UtilisateurEntity.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterSite.tsx";

const ListSite = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const { data } = useGet(url`/api/gestionnaire/get`);

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.GEST_SITE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (siteId) => URLS.UPDATE_SITE(siteId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/site/delete/`,
    });
  }

  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des sites"} />
        <QueryTable
          query={url`/api/site`}
          columns={[
            {
              Header: "Code",
              accessor: "siteCode",
              sortField: "siteCode",
              Filter: <FilterInput type="text" name="siteCode" />,
            },
            {
              Header: "Libellé",
              accessor: "siteLibelle",
              sortField: "siteLibelle",
              Filter: <FilterInput type="text" name="siteLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "siteActif",
              sortField: "siteActif",
              Filter: (
                <SelectEnumOption options={VRAI_FAUX} name={"siteActif"} />
              ),
            }),

            {
              Header: "Gestionnaire",
              accessor: "gestionnaireLibelle",
              sortField: "gestionnaireLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"siteGestionnaireId"}
                  listIdCodeLibelle={data}
                />
              ),
            },
            ActionColumn({
              Header: "Actions",
              accessor: "siteId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableSite"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({
            siteCode: undefined,
            siteLibelle: undefined,
            siteActif: undefined,
            siteGestionnaireId: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListSite;
