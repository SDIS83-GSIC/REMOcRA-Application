import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import CreateButton from "../../../components/Form/CreateButton.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
import {
  BooleanColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import filterValuesToVariable from "./FilterRapportPersonnalise.tsx";

const ListRapportPersonnalise = () => {
  const profilDroitState = useGet(url`/api/profil-droit`);
  const rapportPersonnaliseTypeModule = useGet(
    url`/api/rapport-personnalise/get-type-module`,
  );
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des rapports personnalisés"}
          right={
            <CreateButton
              href={URLS.CREATE_RAPPORT_PERSONNALISE}
              title={"Ajouter un rapport personnalisé"}
            />
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
              Header: "Spatial ?",
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
