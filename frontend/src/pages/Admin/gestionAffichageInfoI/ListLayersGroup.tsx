import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconLayers } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
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
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { URLS } from "../../../routes.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import filterValuesToVariable from "./FilterMetadata.tsx";

type GroupeFonctionnalite = {
  groupeFonctionnaliteId: string;
  profilLibelle: string;
};

const ListLayersGroup = () => {
  const { user } = useAppContext();
  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.CARTO_METADATA_A)) {
    listeButton.push(
      {
        row: (row: any) => {
          return row;
        },
        classEnable: "info",
        route: (styleId) => URLS.UPDATE_LAYER_STYLE(styleId),
        type: TYPE_BUTTON.UPDATE,
      },
      {
        row: (row: any) => {
          return row;
        },
        type: TYPE_BUTTON.DELETE,
        pathname: url`/api/admin/couche/delete/`,
        textDisable: "Impossible de supprimer le style",
      },
    );
  }

  return (
    <Container>
      <PageTitle
        icon={<IconLayers />}
        title={"Gestion des métadonnées des couches"}
        right={
          hasDroit(user, TYPE_DROIT.ADMIN_COUCHE_CARTOGRAPHIQUE) && (
            <CreateButton
              href={URLS.ADD_LAYER_STYLE}
              title={"Ajouter des métadonnées"}
            />
          )
        }
      />

      <QueryTable
        query={url`/api/admin/couche/get-couches-params`}
        columns={[
          {
            Header: "Groupe de couche",
            accessor: "groupeCoucheLibelle",
            sortField: "groupeCoucheLibelle",
            Filter: <FilterInput type="text" name="groupeCoucheLibelle" />,
          },
          {
            Header: "Nom de la couche",
            accessor: "coucheLibelle",
            sortField: "coucheLibelle",
            Filter: <FilterInput type="text" name="coucheLibelle" />,
          },
          {
            Header: "Groupes affiliés",
            accessor: "groupeFonctionnaliteList",
            Cell: (value) => {
              return (
                <div
                  style={{
                    padding: "5px",
                    borderRadius: "4px",
                    fontSize: "14px",
                    color: "#333",
                    lineHeight: "1.5",
                  }}
                >
                  {value?.value
                    .map((item: GroupeFonctionnalite) => item.profilLibelle)
                    .join(", ")}
                </div>
              );
            },
          },
          BooleanColumn({
            Header: "Actif",
            accessor: "coucheMetadataActif",
            sortField: "coucheMetadataActif",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"coucheMetadataActif"}
              />
            ),
          }),
          BooleanColumn({
            Header: "Public",
            accessor: "coucheMetadataPublic",
            sortField: "coucheMetadataPublic",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"coucheMetadataPublic"}
              />
            ),
          }),
          ActionColumn({
            Header: "Actions",
            accessor: "styleId",
            buttons: listeButton,
          }),
        ]}
        idName={"tableCoucheId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          groupeCoucheLibelle: undefined,
          coucheLibelle: undefined,
          groupeFonctionnaliteList: undefined,
          coucheMetadataActif: undefined,
          coucheMetadataPublic: undefined,
        })}
      />
    </Container>
  );
};

export default ListLayersGroup;
