import { useFormikContext } from "formik";
import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import { DateTimeInput } from "../../../components/Form/Form.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconClose,
  IconExportCrise,
  IconMerge,
  IconSee,
  IconWarningCrise,
} from "../../../components/Icon/Icon.tsx";
import useLocalisation, {
  GET_TYPE_GEOMETRY,
} from "../../../components/Localisation/useLocalisation.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import CriseStatutEnum from "../../../Entities/CriseEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime, {
  formatDateTimeForDateTimeInput,
} from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterCrise.tsx";

export const prepareValues = (data: any) => {
  return { criseDateFin: new Date(data.criseDateFin).toISOString() };
};

const ListCrise = () => {
  const { user } = useAppContext();
  const { fetchGeometry } = useLocalisation();

  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.CRISE_R)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.LINK,
      onClick: (criseId, row) => {
        if (row.listeCommune.length > 0) {
          fetchGeometry(
            GET_TYPE_GEOMETRY.COMMUNE_CRISE,
            criseId,
            URLS.OUVRIR_CRISE(criseId),
          );
        }
      },
      icon: <IconSee />,
      textEnable: "Ouvrir la crise",
      classEnable: "primary",
      disable: (v) =>
        [CriseStatutEnum.TERMINEE, CriseStatutEnum.FUSIONNEE].includes(
          CriseStatutEnum[v.original.criseStatutType],
        ),
      textDisable: "Impossible d'ouvrir une crise clôturée",
    });
  }

  if (hasDroit(user, TYPE_DROIT.CRISE_U)) {
    listeButton.push({
      row: (row: any) => {
        return row;
      },
      textEnable: "Modifier",
      disable: (v) =>
        [CriseStatutEnum.TERMINEE, CriseStatutEnum.FUSIONNEE].includes(
          CriseStatutEnum[v.original.criseStatutType],
        ),
      textDisable: "Impossible de modifier une crise qui n'est plus en cours",
      classEnable: "danger",
      route: (criseId) => URLS.UPDATE_CRISE(criseId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row: any) => {
        return row;
      },
      disable: (v) =>
        [CriseStatutEnum.TERMINEE, CriseStatutEnum.FUSIONNEE].includes(
          CriseStatutEnum[v.original.criseStatutType],
        ),
      classEnable: "info",
      textDisable: "Impossible de fusionner une crise qui n'est plus en cours",
      route: (criseId) => URLS.MERGE_CRISE(criseId),
      type: TYPE_BUTTON.LINK,
      icon: <IconMerge />,
      textEnable: "Fusionner",
    });

    listeButton.push({
      row: (row: any) => {
        return row;
      },
      disable: (v) =>
        [CriseStatutEnum.EN_COURS].includes(
          CriseStatutEnum[v.original.criseStatutType],
        ),
      classEnable: "danger",
      textDisable: "Impossible d'exporter une crise qui n'est pas close",
      route: (criseId) => URLS.EXPORT_CRISE(criseId),
      type: TYPE_BUTTON.BUTTON,
      icon: <IconExportCrise />,
      textEnable: "Exporter",
    });

    listeButton.push({
      row: (row: any) => {
        return row;
      },
      type: TYPE_BUTTON.EDIT_MODAL,
      disable: (v) =>
        [CriseStatutEnum.TERMINEE, CriseStatutEnum.FUSIONNEE].includes(
          CriseStatutEnum[v.original.criseStatutType],
        ),
      icon: <IconClose />,
      classEnable: "danger",
      textEnable: "Clore la crise",
      textDisable: "Impossible de clore une crise qui n'est plus en cours",
      editModal: {
        content: () => <PrivateDate />,
        header: (row) => "Clore la crise : " + row.original.criseLibelle,
        path: (row) => `/api/crise/${row.original.criseId}/clore`,
        prepareVariable: (value) => prepareValues(value),
        value: { criseDateFin: formatDateTimeForDateTimeInput(new Date()) },
      },
    });
  }

  return (
    <Container>
      <PageTitle
        icon={<IconWarningCrise />}
        title={"Liste des crises"}
        right={
          hasDroit(user, TYPE_DROIT.CRISE_C) && (
            <CreateButton
              href={URLS.CREATE_CRISE}
              title={"Ajouter une crise"}
            />
          )
        }
      />
      <QueryTable
        query={url`/api/crise`}
        columns={[
          {
            Header: "Type de crise",
            accessor: "typeCriseLibelle",
            sortField: "typeCriseLibelle",
          },
          {
            Header: "Nom",
            accessor: "criseLibelle",
            sortField: "criseLibelle",
            Filter: <FilterInput type="text" name="criseLibelle" />,
          },
          {
            Header: "Description",
            accessor: "criseDescription",
            sortField: "criseDescription",
            Filter: <FilterInput type="text" name="criseDescription" />,
          },
          {
            Header: "Date d'activation",
            accessor: "criseDateDebut",
            sortField: "criseDateDebut",
            Cell: (value) => {
              return (
                <div>{value?.value != null && formatDateTime(value.value)}</div>
              );
            },
          },
          {
            Header: "Date de clôture",
            accessor: "criseDateFin",
            sortField: "criseDateFin",
            Cell: (value) => {
              return (
                <div>{value?.value != null && formatDateTime(value.value)}</div>
              );
            },
          },
          {
            Header: "Statut",
            accessor: "criseStatutType",
            sortField: "criseStatutType",
            Cell: (value) => {
              return (
                <div>
                  {value?.value != null && CriseStatutEnum[value.value]}
                </div>
              );
            },
            Filter: (
              <SelectEnumOption
                options={CriseStatutEnum}
                name={"criseStatutType"}
              />
            ),
          },
          {
            Header: "Communes",
            accessor: "listeCommune",
            Cell: (value) => {
              return <div>{value?.value?.join(", ")}</div>;
            },
          },

          ActionColumn({
            Header: "Actions",
            accessor: "criseId",
            buttons: listeButton,
          }),
        ]}
        idName={"tableCriseId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          typeCriseLibelle: undefined,
          criseLibelle: undefined,
          criseDescription: undefined,
          criseStatutType: undefined,
          criseDateDebut: undefined,
          criseDateFin: undefined,
        })}
      />
    </Container>
  );
};

const PrivateDate = () => {
  const { values } = useFormikContext();

  return (
    <DateTimeInput
      name="criseDateFin"
      label="Date et heure de fin"
      required={true}
      value={values.criseDateFin}
    />
  );
};

export default ListCrise;
