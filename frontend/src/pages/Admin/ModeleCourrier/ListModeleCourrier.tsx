import { Container } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
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
import filterValuesToVariable from "./FilterModeleCourrier.tsx";

const ListModeleCourrier = () => {
  const profilDroitState = useGet(url`/api/profil-droit`);
  const modeleCourrierTypeModule = useGet(url`/api/modules/get-type-module`);
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Modèles de courrier"}
          right={
            <CreateButton
              title={"Ajouter un modèle de courrier"}
              href={URLS.CREATE_MODELE_COURRIER}
            />
          }
        />

        <QueryTable
          query={url`/api/courriers/modeles`}
          columns={[
            {
              Header: "Code",
              accessor: "modeleCourrierCode",
              sortField: "modeleCourrierCode",
              Filter: <FilterInput type="text" name="modeleCourrierCode" />,
            },
            {
              Header: "Libellé",
              accessor: ({
                modeleCourrierId,
                modeleCourrierLibelle,
                modeleCourrierDescription,
              }) => {
                return {
                  modeleCourrierId,
                  modeleCourrierLibelle,
                  modeleCourrierDescription,
                };
              },
              sortField: "modeleCourrierLibelle",
              Cell: (value) => {
                return (
                  <>
                    {value.value.modeleCourrierLibelle}
                    {value.value.modeleCourrierDescription && (
                      <TooltipCustom
                        tooltipText={value.value.modeleCourrierDescription}
                        tooltipId={value.value.modeleCourrierId}
                      >
                        <IconInfo />
                      </TooltipCustom>
                    )}
                  </>
                );
              },
              Filter: <FilterInput type="text" name="modeleCourrierLibelle" />,
            },
            {
              Header: "Module",
              accessor: "modeleCourrierModule",
              Filter: (
                <SelectFilterFromList
                  name={"modeleCourrierModule"}
                  listIdCodeLibelle={modeleCourrierTypeModule.data?.map(
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
              Header: "Actif",
              accessor: "modeleCourrierActif",
              sortField: "modeleCourrierActif",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"modeleCourrierActif"}
                />
              ),
            }),
            ProtectedColumn({
              Header: "Protégé",
              accessor: "modeleCourrierProtected",
              sortField: "modeleCourrierProtected",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"modeleCourrierProtected"}
                />
              ),
            }),
            ActionColumn({
              Header: "Actions",
              accessor: "modeleCourrierId",
              buttons: [
                {
                  row: (row) => {
                    return row;
                  },
                  route: (modeleCourrierId) =>
                    URLS.UPDATE_MODELE_COURRIER(modeleCourrierId),
                  type: TYPE_BUTTON.UPDATE,
                },
                {
                  row: (row) => {
                    return row;
                  },
                  type: TYPE_BUTTON.DELETE,
                  pathname: url`/api/courriers/modele-courrier/delete/`,
                },
              ],
            }),
          ]}
          idName={"tableModeleCourrier"}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListModeleCourrier;
