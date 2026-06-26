import { Container } from "react-bootstrap";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import {
  IconEdit,
  IconLocation,
  IconMerge,
} from "../../components/Icon/Icon.tsx";
import useLocalisation, {
  GET_TYPE_GEOMETRY,
} from "../../components/Localisation/useLocalisation.tsx";
import { ActionColumn } from "../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../droits.tsx";
import { DfciConflitEntity } from "../../Entities/DfciConflitEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import { FilterDfciConflitProperty } from "./FilterDfciConflit.tsx";

enum DFCI_TABLE_NOM {
  DFCI_AIRE = "dfci_aire",
  DFCI_PISTE = "dfci_piste",
  DFCI_DEB = "dfci_deb",
  DFCI_PANNEAU = "dfci_panneau",
}

const DfciGestionConflits = () => {
  const listeButton: ButtonType[] = [];
  const { user } = useAppContext();
  const { fetchGeometry } = useLocalisation();

  function onClickLocalisation(typeGeom: GET_TYPE_GEOMETRY, elementId: string) {
    fetchGeometry(typeGeom, elementId, URLS.CARTE_DFCI);
  }

  if (hasDroit(user, TYPE_DROIT.DFCI_GESTION_CONFLITS_A)) {
    listeButton.push({
      row: (row: DfciConflitEntity) => {
        return row;
      },
      route: (dfciConflitId) => URLS.RESOLVE_DFCI_CONFLIT(dfciConflitId),
      icon: <IconEdit />,
      type: TYPE_BUTTON.LINK,
      textEnable: "Corriger le conflit",
    });
  }

  if (hasDroit(user, TYPE_DROIT.DFCI_R)) {
    listeButton.push({
      row: (row: DfciConflitEntity) => {
        return row;
      },
      icon: <IconLocation />,
      type: TYPE_BUTTON.LINK,
      textEnable: "Localiser l'élément",
      onClick: (idConflit, row) => {
        switch (row.dfciConflitTable) {
          case DFCI_TABLE_NOM.DFCI_PISTE:
            onClickLocalisation(
              GET_TYPE_GEOMETRY.DFCI_PISTE,
              row.dfciConflitElementId,
            );
            break;
          case DFCI_TABLE_NOM.DFCI_DEB:
            onClickLocalisation(
              GET_TYPE_GEOMETRY.DFCI_DEB,
              row.dfciConflitElementId,
            );
            break;
          case DFCI_TABLE_NOM.DFCI_AIRE:
            onClickLocalisation(
              GET_TYPE_GEOMETRY.DFCI_AIRE,
              row.dfciConflitElementId,
            );
            break;
          case DFCI_TABLE_NOM.DFCI_PANNEAU:
            onClickLocalisation(
              GET_TYPE_GEOMETRY.DFCI_PANNEAU,
              row.dfciConflitElementId,
            );
            break;
        }
      },
    });
  }

  const filterContext = useFilterContext({
    dfciConflitTable: undefined,
    dfciConflitElementId: undefined,
    dfciConflitChamp: undefined,
  });

  return (
    <>
      <Container fluid className={"px-5"}>
        <PageTitle icon={<IconMerge />} title={"Gestion des conflits"} />
        <QueryTable
          query={url`/api/dfci-conflit/list`}
          columns={[
            {
              Header: "Table",
              accessor: "dfciConflitTable",
              sortField: "dfciConflitTable",
              Filter: <FilterInput type="text" name="dfciConflitTable" />,
            },
            {
              Header: "ID élément",
              accessor: "dfciConflitElementId",
              sortField: "dfciConflitElementId",
              Filter: <FilterInput type="text" name="dfciConflitElementId" />,
            },
            {
              Header: "Champ",
              accessor: "dfciConflitChamp",
              sortField: "dfciConflitChamp",
              Filter: <FilterInput type="text" name="dfciConflitChamp" />,
            },
            {
              Header: "Valeur REMOcRA",
              accessor: "dfciConflitValeurRemocra",
            },
            {
              Header: "Valeur SIG",
              accessor: "dfciConflitValeurSig",
            },
            {
              Header: "Date du conflit",
              accessor: "dfciConflitDate",
              sortField: "dfciConflitDate",
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
              accessor: "dfciConflitId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableDfciConflitId"}
          filterValuesToVariable={FilterDfciConflitProperty}
          filterContext={filterContext}
        />
      </Container>
    </>
  );
};
export default DfciGestionConflits;
