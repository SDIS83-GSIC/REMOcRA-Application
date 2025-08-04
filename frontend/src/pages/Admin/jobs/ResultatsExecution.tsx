import { Formik } from "formik";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import EtatJobEnum from "../../../enums/EtatJobEnum.tsx";
import TaskType from "../../../enums/TaskTypeEnum.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterResultatsExecution.tsx";

const ResultatsExecution = () => {
  return (
    <>
      <PageTitle title={"Résultats d'exécution"} icon={<IconList />} />
      <Formik initialValues={{ typeTache: "" }} onSubmit={() => {}}>
        <FormTable />
      </Formik>
    </>
  );
};

export default ResultatsExecution;

const FormTable = () => {
  const listeButton: ButtonType[] = [];
  listeButton.push({
    row: (row) => {
      return row;
    },
    route: (jobId) => URLS.RESULTATS_EXECUTION_LOGLINES(jobId),
    type: TYPE_BUTTON.SEE,
  });

  const listeTypeTask = Object.entries(TaskType).map(([, e]) => ({
    id: e.id,
    code: e.code,
    libelle: e.label,
  }));

  return (
    <QueryTable
      query="/api/jobs/list"
      getCount={(data) => data?.count ?? 0}
      getList={(data) => data?.list ?? []}
      columns={[
        {
          Header: "Type de tâche",
          accessor: "typeTask",
          sortField: "typeTask",
          Cell: (value) => {
            return <div>{TaskType[value?.value]?.label ?? value?.value}</div>;
          },
          Filter: (
            <SelectFilterFromList
              listIdCodeLibelle={listeTypeTask.sort((a, b) =>
                a.libelle.localeCompare(b.libelle),
              )}
              name={"typeTask"}
            />
          ),
        },
        {
          Header: "Date de début",
          accessor: "jobDateDebut",
          sortField: "jobDateDebut",
          Cell: (value) => {
            return (
              <div>{value?.value != null && formatDateTime(value.value)}</div>
            );
          },
        },
        {
          Header: "Date de fin",
          accessor: "jobDateFin",
          sortField: "jobDateFin",
          Cell: (value) => {
            return (
              <div>{value?.value != null && formatDateTime(value.value)}</div>
            );
          },
        },
        {
          Header: "État",
          accessor: "jobEtatJob",
          sortField: "jobEtatJob",
          Cell: (value) => {
            return <div>{EtatJobEnum[value?.value] ?? value?.value}</div>;
          },
          Filter: <SelectEnumOption options={EtatJobEnum} name={"etatJob"} />,
        },
        ActionColumn({
          Header: "Actions",
          accessor: "jobId",
          buttons: listeButton,
        }),
      ]}
      idName="jobTable"
      filterValuesToVariable={filterValuesToVariable}
      filterContext={useFilterContext({
        typeTask: undefined,
        etatJob: undefined,
      })}
      queryParams={[]}
      asyncOptions={[]}
    />
  );
};
