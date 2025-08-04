import { Col, Container, Row } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconDuplicate,
  IconExport,
  IconInfo,
  IconList,
} from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import filterValuesToVariable from "./FilterRapportPersonnalise.tsx";

const ListRapportPersonnalise = () => {
  const profilDroitState = useGet(url`/api/profil-droit`);
  const rapportPersonnaliseTypeModule = useGet(
    url`/api/modules/get-type-module`,
  );
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Rapports personnalisés"}
          right={
            <Row>
              <Col>
                <CreateButton
                  href={URLS.CREATE_RAPPORT_PERSONNALISE}
                  title={"Ajouter un rapport"}
                />
              </Col>
              <Col>
                <CustomLinkButton
                  variant="primary"
                  pathname={URLS.IMPORTER_RAPPORT_PERSONNALISE}
                >
                  Importer
                </CustomLinkButton>
              </Col>
            </Row>
          }
        />

        <QueryTable
          query={url`/api/rapport-personnalise`}
          columns={[
            {
              Header: "Code",
              accessor: "rapportPersonnaliseCode",
              sortField: "rapportPersonnaliseCode",
              Filter: (
                <FilterInput type="text" name="rapportPersonnaliseCode" />
              ),
            },
            {
              Header: "Libellé",
              accessor: ({
                rapportPersonnaliseId,
                rapportPersonnaliseLibelle,
                rapportPersonnaliseDescription,
              }) => {
                return {
                  rapportPersonnaliseId,
                  rapportPersonnaliseLibelle,
                  rapportPersonnaliseDescription,
                };
              },
              sortField: "rapportPersonnaliseLibelle",
              Cell: (value) => {
                return (
                  <>
                    {value.value.rapportPersonnaliseLibelle}
                    {value.value.rapportPersonnaliseDescription && (
                      <TooltipCustom
                        tooltipText={value.value.rapportPersonnaliseDescription}
                        tooltipId={value.value.rapportPersonnaliseId}
                      >
                        <IconInfo />
                      </TooltipCustom>
                    )}
                  </>
                );
              },
              Filter: (
                <FilterInput type="text" name="rapportPersonnaliseLibelle" />
              ),
            },
            {
              Header: "Module",
              accessor: "rapportPersonnaliseModule",
              Filter: (
                <SelectFilterFromList
                  name={"rapportPersonnaliseModule"}
                  listIdCodeLibelle={rapportPersonnaliseTypeModule.data?.map(
                    (e) => ({ id: e, code: e, libelle: e }),
                  )}
                />
              ),
            },
            {
              Header: "Profils droits",
              accessor: "listeProfilDroit",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listeProfilDroitId"}
                  listIdCodeLibelle={profilDroitState.data}
                />
              ),
            },
            BooleanColumn({
              Header: "Spatial",
              accessor: ({ rapportPersonnaliseChampGeometrie }) => {
                return rapportPersonnaliseChampGeometrie != null;
              },
              sortField: "rapportPersonnaliseChampGeometrie",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"rapportPersonnaliseChampGeometrie"}
                />
              ),
            }),
            BooleanColumn({
              Header: "Actif",
              accessor: "rapportPersonnaliseActif",
              sortField: "rapportPersonnaliseActif",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"rapportPersonnaliseActif"}
                />
              ),
            }),
            ProtectedColumn({
              Header: "Protégé",
              accessor: "rapportPersonnaliseProtected",
              sortField: "rapportPersonnaliseProtected",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"rapportPersonnaliseProtected"}
                />
              ),
            }),
            ActionColumn({
              Header: "Actions",
              accessor: "rapportPersonnaliseId",
              buttons: [
                {
                  row: (row) => {
                    return row;
                  },
                  route: (rapportPersonnaliseId) =>
                    URLS.UPDATE_RAPPORT_PERSONNALISE(rapportPersonnaliseId),
                  type: TYPE_BUTTON.UPDATE,
                },
                {
                  row: (row) => {
                    return row;
                  },
                  type: TYPE_BUTTON.DELETE,
                  pathname: url`/api/rapport-personnalise/delete/`,
                  disable: (row) => row.original.rapportPersonnaliseProtected,
                  textDisable: "Impossible de supprimer un rapport protégé",
                },
                {
                  row: (row) => {
                    return row;
                  },
                  route: (rapportPersonnaliseId) =>
                    URLS.DUPLICATE_RAPPORT_PERSONNALISE(rapportPersonnaliseId),
                  type: TYPE_BUTTON.LINK,
                  textEnable: "Dupliquer le rapport",
                  icon: <IconDuplicate />,
                  classEnable: "warning",
                },
                {
                  row: (row) => {
                    return row;
                  },
                  route: (rapportPersonnaliseId) =>
                    url`/api/rapport-personnalise/export/` +
                    rapportPersonnaliseId,
                  type: TYPE_BUTTON.BUTTON,
                  textEnable: "Exporter le rapport",
                  icon: <IconExport />,
                  disable: (row) => row.original.rapportPersonnaliseProtected,
                  textDisable: "Impossible d'exporter un rapport protégé",
                  classEnable: "warning",
                },
              ],
            }),
          ]}
          idName={"tableRapportPersonnalise"}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListRapportPersonnalise;
