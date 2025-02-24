import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { DateTimeInput } from "../../../components/Form/Form.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconWarningCrise, IconClose } from "../../../components/Icon/Icon.tsx";
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
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import filterValuesToVariable from "./FilterCrise.tsx";

export const prepareValues = (data: any) => ({
  criseDateFin: new Date(data.criseDateFin).toISOString(),
});

const ListCrise = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.CRISE_U)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      textEnable: "Modifier",
      route: (criseId) => URLS.UPDATE_CRISE(criseId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.EDIT_MODAL,
      disable: (v) => {
        return (
          CriseStatutEnum[v.original.criseStatut] === CriseStatutEnum.TERMINEE
        );
      },
      icon: <IconClose />,
      classEnable: "danger",
      textEnable: "Clore la crise",
      textDisable: "Impossible de clore une crise qui n'est plus en cours",
      editModal: {
        content: () => (
          <DateTimeInput
            name="criseDateFin"
            label="Date et heure de fin"
            required={true}
          />
        ),
        header: (row) => "Clore la crise : " + row.original.criseLibelle,
        path: (row) => `/api/crise/${row.original.criseId}/clore`,
        prepareVariable: (value) => prepareValues(value),
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

export default ListCrise;
