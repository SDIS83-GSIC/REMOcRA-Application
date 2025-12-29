import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconInfo,
  IconMapComponent,
  IconSortList,
} from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import SOURCE_CARTO from "../../../enums/SourceCartoEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import filterValuesToVariable from "./FilterCouche.tsx";

const ListCouche = () => {
  const { groupeCoucheId } = useParams<{ groupeCoucheId: string }>();
  const { data } = useGet(`/api/admin/groupe-couche/${groupeCoucheId}`);

  const groupeFonctionnalitesState = useGet(url`/api/groupe-fonctionnalites`);
  const typeModuleState = useGet(url`/api/modules/get-type-module-accueil`);
  const listeButton: ButtonType[] = [];

  listeButton.push({
    row: (row) => {
      return row;
    },
    type: TYPE_BUTTON.UPDATE,
    route: (data) => URLS.UPDATE_COUCHE(groupeCoucheId!, data),
  });

  listeButton.push({
    row: (row) => {
      return row;
    },
    textDisable: "Cette couche est protégée et ne peut pas être supprimée",
    disable: (row) => row.original.coucheProtected,
    textEnable: "Supprimer la couche",
    type: TYPE_BUTTON.DELETE,
    pathname: url`/api/admin/couche/groupe-couche/${groupeCoucheId}/delete/`,
  });

  return (
    <Container fluid>
      <PageTitle
        title={`Couches du groupe ${data?.groupeCoucheCode} - ${data?.groupeCoucheLibelle}`}
        icon={<IconMapComponent />}
        right={
          <>
            <CustomLinkButton
              pathname={URLS.SORT_COUCHE(groupeCoucheId!)}
              variant={"primary"}
              className="me-2"
            >
              <IconSortList /> Réordonner les couches du groupe
            </CustomLinkButton>
            <CreateButton
              href={URLS.CREATE_COUCHE(groupeCoucheId!)}
              title={"Ajouter une couche au groupe"}
            />
          </>
        }
      />
      <QueryTable
        query={url`/api/admin/couche/groupe-couche/${groupeCoucheId!}`}
        columns={[
          {
            Header: "Code",
            accessor: "coucheCode",
            sortField: "coucheCode",
            Filter: <FilterInput type="text" name="coucheCode" />,
          },
          {
            Header: "Libellé",
            accessor: "coucheLibelle",
            sortField: "coucheLibelle",
            Filter: <FilterInput type="text" name="coucheLibelle" />,
          },
          {
            Header: "Source",
            accessor: ({
              coucheSource,
              coucheTuilage,
              coucheId,
            }: {
              coucheSource: string;
              coucheTuilage: string;
              coucheId: string;
            }) => {
              return { coucheSource, coucheTuilage, coucheId };
            },
            sortField: "coucheSource",
            Filter: <FilterInput type="text" name="coucheSource" />,
            Cell: ({
              value,
            }: {
              value: {
                coucheSource: string;
                coucheTuilage: string;
                coucheId: string;
              };
            }) =>
              value.coucheSource === SOURCE_CARTO.WMS ? (
                <TooltipCustom
                  tooltipId={value.coucheId}
                  tooltipText={
                    value.coucheTuilage ? "Avec tuilage" : "Sans tuilage"
                  }
                >
                  <span>
                    {value.coucheSource} <IconInfo />
                  </span>
                </TooltipCustom>
              ) : (
                value.coucheSource
              ),
          },
          {
            Header: "Nom",
            accessor: "coucheNom",
            sortField: "coucheNom",
            Filter: <FilterInput type="text" name="coucheNom" />,
          },
          {
            Header: "EPSG",
            accessor: "coucheProjection",
            sortField: "coucheProjection",
            Filter: <FilterInput type="text" name="coucheProjection" />,
          },
          {
            Header: "Format",
            accessor: "coucheFormat",
            sortField: "coucheFormat",
            Filter: <FilterInput type="text" name="coucheFormat" />,
          },
          {
            Header: "Modules",
            accessor: "moduleList",
            sortField: "moduleList",

            Filter: (
              <MultiSelectFilterFromList
                name={"moduleList"}
                listIdCodeLibelle={typeModuleState?.data?.map((typeModule) => ({
                  id: typeModule,
                  libelle: typeModule,
                }))}
              />
            ),
          },
          {
            Header: (
              <>
                {" "}
                Profils sur ZC
                <TooltipCustom
                  tooltipId="tooltip-profil-hors-zc"
                  tooltipText="Profils pour lesquels la couche est visible uniquement sur la zone de compétence de l'utilisateur connecté"
                >
                  <IconInfo />
                </TooltipCustom>
              </>
            ),
            accessor: "groupeFonctionnalitesZc",
            sortField: "groupeFonctionnalitesZc",
            Filter: (
              <MultiSelectFilterFromList
                name={"groupeFonctionnalitesZc"}
                listIdCodeLibelle={groupeFonctionnalitesState.data}
              />
            ),
            Cell: (row: any) => {
              return (
                <TooltipCustom
                  tooltipText={row.value}
                  tooltipId={row.value}
                  maxWidth={100}
                >
                  {row.value}
                </TooltipCustom>
              );
            },
          },
          {
            Header: (
              <>
                Profils hors ZC
                <TooltipCustom
                  tooltipId="tooltip-profil-hors-zc"
                  tooltipText="Profils pour lesquels la couche est complètement visible, sans prendre en compte la zone de compétence de l'utilisateur connecté"
                >
                  <IconInfo />
                </TooltipCustom>
              </>
            ),
            accessor: "groupeFonctionnalitesHorsZc",
            sortField: "groupeFonctionnalitesHorsZc",
            Filter: (
              <MultiSelectFilterFromList
                name={"groupeFonctionnalitesHorsZc"}
                listIdCodeLibelle={groupeFonctionnalitesState.data}
              />
            ),
            Cell: (row: any) => {
              return (
                <TooltipCustom
                  tooltipText={row.value}
                  tooltipId={row.value}
                  maxWidth={100}
                >
                  {row.value}
                </TooltipCustom>
              );
            },
          },
          BooleanColumn({
            Header: "Publique",
            accessor: "couchePublic",
            sortField: "couchePublic",
            Filter: (
              <SelectEnumOption options={VRAI_FAUX} name={"couchePublic"} />
            ),
          }),
          BooleanColumn({
            Header: "Proxy",
            accessor: "coucheProxy",
            sortField: "coucheProxy",
            Filter: (
              <SelectEnumOption options={VRAI_FAUX} name={"coucheProxy"} />
            ),
          }),
          BooleanColumn({
            Header: "Active par défaut",
            accessor: "coucheActive",
            sortField: "coucheActive",
            Filter: (
              <SelectEnumOption options={VRAI_FAUX} name={"coucheActive"} />
            ),
          }),
          ProtectedColumn({
            Header: "Protégé",
            accessor: "coucheProtected",
            sortField: "coucheProtected",
            Filter: (
              <SelectEnumOption options={VRAI_FAUX} name={"coucheProtected"} />
            ),
          }),
          ActionColumn({
            Header: "Actions",
            accessor: "coucheId",
            buttons: listeButton,
          }),
        ]}
        idName={"tableCouche"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          coucheLibelle: undefined,
          coucheCode: undefined,
        })}
      />
    </Container>
  );
};

export default ListCouche;
