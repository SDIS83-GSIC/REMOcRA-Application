import { Formik } from "formik";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import EtatJobEnum from "../../../enums/EtatJobEnum.tsx";
import TaskType from "../../../enums/TaskTypeEnum.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
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
  const typeTaskState = useGet(url`/api/jobs/types-task`);
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
          Filter: typeTaskState?.data && (
            <SelectFilterFromList
              listIdCodeLibelle={typeTaskState?.data.map((e) => ({
                id: e,
                code: e,
                libelle: TaskType[e]?.label ?? e,
              }))}
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
