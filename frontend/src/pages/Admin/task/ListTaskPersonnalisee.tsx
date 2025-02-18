import { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { TaskPersonnaliseEntity } from "../../../Entities/TaskEntity.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconOverview } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import ParametreTaskForm, {
  getCronTab,
  getInitialValues,
} from "./ParametreTaskForm.tsx";

const ListeTaskPersonnalisee = () => {
  const [currentTask, setCurrentTask] = useState<TaskPersonnaliseEntity | null>(
    null,
  );

  const taskInfo = useGet(url`/api/task/personnalisees`);

  if (!taskInfo.isResolved) {
    return;
  }

  const listTasks: TaskPersonnaliseEntity[] = taskInfo.data.map((element) => ({
    taskId: element.taskId,
    taskActif: element.taskActif,
    taskPlanification: element.taskPlanification,
    taskLibelle: element.taskLibelle,
    taskParametres: element.taskParametres,
    isPlanificationEnabled: element.taskPlanification != null,
  }));

  return (
    <Container>
      <PageTitle
        icon={<IconOverview />}
        title={"Paramétrage des traitements spécifiques"}
      />
      <Row>
        <Col xs="5">
          <div className="bg-light p-2 border rounded mx-2">
            {listTasks.map((e) => (
              <Row key={e.taskLibelle}>
                <CustomLinkButton
                  key={e.taskLibelle}
                  onClick={() => setCurrentTask(e)}
                >
                  {e.taskLibelle}
                </CustomLinkButton>
              </Row>
            ))}
          </div>
        </Col>
        {currentTask && (
          <Col className="bg-light p-2 border rounded mx-2">
            <h3>{currentTask.taskLibelle}</h3>
            <MyFormik
              initialValues={getInitialValues(
                listTasks.find((e) => e.taskId === currentTask.taskId),
              )}
              isPost={false}
              submitUrl={"/api/task/personnalisee"}
              prepareVariables={(values) => {
                const formData = new FormData();
                formData.append("taskId", values.taskId);
                formData.append("taskActif", values.taskActif);
                formData.append(
                  "taskPlanification",
                  values.isPlanificationEnabled &&
                    getCronTab(values).trim().length > 0
                    ? getCronTab(values).trim()
                    : "null",
                );
                formData.append(
                  "taskParametres",
                  JSON.stringify(values.taskParametres),
                );
                formData.append("zipFile", values.zipFile);

                return formData;
              }}
              isMultipartFormData={true}
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

export default ListeTaskPersonnalisee;
