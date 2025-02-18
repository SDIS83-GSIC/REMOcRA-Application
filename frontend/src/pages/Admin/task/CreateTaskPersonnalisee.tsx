import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconOverview } from "../../../components/Icon/Icon.tsx";
import ParametreTaskForm, {
  getCronTab,
  getInitialValues,
} from "./ParametreTaskForm.tsx";

const CreateTaskPersonnalisee = () => {
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconOverview />}
          title={"Ajouter une tâche spécifique"}
        />
      </Container>
      <MyFormik
        initialValues={getInitialValues(null)}
        isPost={true}
        submitUrl={"/api/task/personnalisee/create"}
        prepareVariables={(values) => {
          const formData = new FormData();
          formData.append("taskActif", values.taskActif);
          formData.append(
            "taskPlanification",
            values.isPlanificationEnabled &&
              getCronTab(values).trim().length > 0
              ? getCronTab(values)!.trim()
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
    </>
  );
};

export default CreateTaskPersonnalisee;
