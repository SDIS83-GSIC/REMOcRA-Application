import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import LOG_LINE_GRAVITY from "../../../enums/LogLineGravityEnum.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterLogLines.tsx";

const LogLines = () => {
  const { jobId } = useParams();

  return (
    <>
      <Container>
        <PageTitle
          title={"Détails de l'exécution du job " + jobId}
          icon={<IconList />}
        />
      </Container>
      <Container fluid className={"px-5"}>
        <QueryTable
          query={url`/api/jobs/log-lines/${jobId}`}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({})}
          idName={"ListLogLines"}
          columns={[
            {
              Header: "Date",
              accessor: "logLineDate",
              sortField: "logLineDate",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && formatDateTime(value.value)}
                  </div>
                );
              },
              width: 200,
            },
            {
              Header: "Niveau de gravité",
              accessor: "logLineGravity",
              sortField: "logLineGravity",
              Filter: (
                <SelectEnumOption
                  options={LOG_LINE_GRAVITY}
                  name={"logLineGravity"}
                />
              ),
              width: 200,
            },
            {
              Header: "Message",
              accessor: "logLineMessage",
            },
          ]}
        />
      </Container>
    </>
  );
};

export default LogLines;
