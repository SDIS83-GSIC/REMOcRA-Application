import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import Crise, {
  getInitialValues,
  prepareCriseValues,
  criseValidationSchema,
} from "./Crise.tsx";

const CreateCrise = () => {
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'une crise"} />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareCriseValues(values)}
        validationSchema={criseValidationSchema}
        submitUrl={`/api/crise/create`}
        isPost={true}
        redirectUrl={URLS.LIST_CRISES}
      >
        <Crise />
      </MyFormik>
    </Container>
  );
};

export default CreateCrise;
