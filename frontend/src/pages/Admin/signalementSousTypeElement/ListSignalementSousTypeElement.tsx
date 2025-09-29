import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconSignalement } from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  columnType,
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import { referenceTypeGeometrie } from "../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";

const ListSignalementSousTypeElement = () => {
  const { user } = useAppContext();
  const listeTypeElement: IdCodeLibelleType[] = useGet(
    url`/api/signalement-sous-type-element/ref`,
  ).data;

  const listeButton: ButtonType[] = [];

  const column: Array<columnType> = [
    {
      Header: "Code",
      accessor: "signalementSousTypeElementCode",
      sortField: "signalementSousTypeElementCode",
      Filter: <FilterInput type="text" name="signalementSousTypeElementCode" />,
    },
    {
      Header: "Libellé",
      accessor: "signalementSousTypeElementLibelle",
      sortField: "signalementSousTypeElementLibelle",
      Filter: (
        <FilterInput type="text" name="signalementSousTypeElementLibelle" />
      ),
    },
    BooleanColumn({
      Header: "Actif",
      accessor: "signalementSousTypeElementActif",
      sortField: "signalementSousTypeElementActif",
      Filter: (
        <SelectEnumOption
          options={VRAI_FAUX}
          name={"signalementSousTypeElementActif"}
        />
      ),
    }),
    {
      Header: "Type élément",
      accessor: "signalementTypeElementLibelle",
      sortField: "signalementTypeElementLibelle",
      Filter: (
        <SelectFilterFromList
          name={"signalementTypeElementId"}
          listIdCodeLibelle={listeTypeElement}
        />
      ),
    },
    {
      Header: "Type géométrie",
      accessor: "signalementSousTypeElementTypeGeometrie",
      sortField: "signalementSousTypeElementTypeGeometrie",
      Cell: (value) => {
        return (
          <div>
            {referenceTypeGeometrie.find((e) => e.code === value.value).libelle}
          </div>
        );
      },
      Filter: (
        <SelectFilterFromList
          name={"signalementSousTypeElementTypeGeometrie"}
          listIdCodeLibelle={referenceTypeGeometrie}
        />
      ),
    },
    ActionColumn({
      Header: "Actions",
      accessor: "signalementSousTypeElementId",
      buttons: listeButton,
    }),
  ];

  if (hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI)) {
    listeButton.push({
      disable: (v) => v.original.isUsed,
      textDisable: `Impossible de modifier l'élément car il est utilisé dans une alerte déjà déclarée.`,
      row: (row) => {
        return row;
      },
      route: (signalementSousTypeElementId) =>
        URLS.UPDATE_SIGNALEMENT_SOUS_TYPE_ELEMENT(signalementSousTypeElementId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      disable: (v) => v.original.isUsed,
      textDisable: `Impossible de supprimer l'élément car il est utilisé dans une alerte déjà déclarée.`,
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/signalement-sous-type-element/delete/`,
    });
  }

  return (
    <Container>
      <PageTitle
        title="Signalement - Sous type élément"
        icon={<IconSignalement />}
        urlRetour={URLS.MODULE_ADMIN}
        right={
          <CreateButton
            title="Ajouter un sous type d'élément"
            href={URLS.ADD_SIGNALEMENT_SOUS_TYPE_ELEMENT}
          />
        }
      />
      <QueryTable
        columns={column}
        query={url`/api/signalement-sous-type-element/get`}
        idName="ListNomenclature"
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          signalementSousTypeElementCode: undefined,
          signalementSousTypeElementLibelle: undefined,
          signalementSousTypeElementActif: undefined,
          signalementTypeElementId: undefined,
          signalementSousTypeElementTypeGeometrie: undefined,
        })}
      />
    </Container>
  );
};

export default ListSignalementSousTypeElement;

type filterSousTypeElement = {
  signalementSousTypeElementCode?: string;
  signalementSousTypeElementLibelle?: string;
  signalementSousTypeElementActif?: string;
  signalementTypeElementId?: string;
  signalementSousTypeElementTypeGeometrie?: string;
};

const filterValuesToVariable = ({
  signalementSousTypeElementCode,
  signalementSousTypeElementLibelle,
  signalementSousTypeElementActif,
  signalementTypeElementId,
  signalementSousTypeElementTypeGeometrie,
}: filterSousTypeElement) => {
  const filter: filterSousTypeElement = {};

  if (
    signalementSousTypeElementCode != null &&
    signalementSousTypeElementCode.trim() !== ""
  ) {
    filter.signalementSousTypeElementCode = signalementSousTypeElementCode;
  }
  if (
    signalementSousTypeElementLibelle != null &&
    signalementSousTypeElementLibelle.trim() !== ""
  ) {
    filter.signalementSousTypeElementLibelle =
      signalementSousTypeElementLibelle;
  }
  if (
    signalementSousTypeElementActif != null &&
    signalementSousTypeElementActif.trim() !== ""
  ) {
    filter.signalementSousTypeElementActif = signalementSousTypeElementActif;
  }
  if (
    signalementTypeElementId != null &&
    signalementTypeElementId.trim() !== ""
  ) {
    filter.signalementTypeElementId = signalementTypeElementId;
  }
  if (
    signalementSousTypeElementTypeGeometrie != null &&
    signalementSousTypeElementTypeGeometrie.trim() !== ""
  ) {
    filter.signalementSousTypeElementTypeGeometrie =
      signalementSousTypeElementTypeGeometrie;
  }

  return filter;
};
