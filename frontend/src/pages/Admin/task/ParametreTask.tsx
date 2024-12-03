import { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { TaskEntity } from "../../../Entities/TaskEntity.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.js";
import CustomLinkButton from "../../../components/Form/CustomLinkButton.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconOverview } from "../../../components/Icon/Icon.tsx";
import TaskType from "../../../enums/TaskTypeEnum.tsx";
import url from "../../../module/fetch.tsx";
import ParametreTaskForm, {
  getInitialValues,
  prepareVariables,
} from "./ParametreTaskForm.tsx";

const ListeTask = () => {
  const [currentTaskTypeState, setCurrentTaskType] = useState<string>(null);

  const taskInfo = useGet(url`/api/task/`);

  if (!taskInfo.isResolved) {
    return;
  }

  const listTasks: TaskEntity = taskInfo.data.map((element) => ({
    taskId: element.taskId,
    taskType: element.taskType,
    taskActif: element.taskActif,
    taskPlanification: element.taskPlanification,
    taskExecManuelle: element.taskExecManuelle,
    taskParametres: element.taskParametres,
    taskNotification: element.taskNotification,

    isPlanificationEnabled: element.taskPlanification != null,
    //everyMinute: null,
  }));

  return (
    <Container>
      <PageTitle
        icon={<IconOverview />}
        title={"Parametrage des tâches planifiées"}
      />
      <Row>
        <Col xs="5">
          <div className="bg-light p-2 border rounded mx-2">
            {Object.keys(TaskType).map((key) => (
              <Row key={key}>
                <CustomLinkButton
                  key={key}
                  onClick={() => setCurrentTaskType(key)}
                >
                  {TaskType[key].label}
                </CustomLinkButton>
              </Row>
            ))}
          </div>
        </Col>
        {currentTaskTypeState && (
          <Col className="bg-light p-2 border rounded mx-2">
            <h3>{TaskType[currentTaskTypeState].label}</h3>
            <MyFormik
              initialValues={getInitialValues(
                listTasks.find((e) => e.taskType === currentTaskTypeState),
              )}
              isPost={false}
              submitUrl={"/api/task"}
              prepareVariables={(values) => prepareVariables(values)}
              onSubmit={() => window.location.reload()}
            >
              <ParametreTaskForm />
            </MyFormik>
          </Col>
        )}
      </Row>
    </Container>
  );
};

export default ListeTask;
