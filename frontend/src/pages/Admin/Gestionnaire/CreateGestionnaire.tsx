import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import Gestionnaire, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Gestionnaire.tsx";

const CreateGestionnaire = () => {
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'un gestionnaire"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/gestionnaire/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_GESTIONNAIRE}
      >
        <Gestionnaire />
      </MyFormik>
    </Container>
  );
};

export default CreateGestionnaire;
