import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ModeleCourrier, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./ModeleCourrier.tsx";

const CreateModeleCourrier = () => {
  return (
    <Container>
      <PageTitle
        title={"Création d'un modèle de courrier"}
        icon={<IconCreate />}
      />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareVariables(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/courriers/modeles/create/`}
        isPost={true}
        isMultipartFormData={true}
        redirectUrl={URLS.LIST_MODELE_COURRIER}
      >
        <ModeleCourrier />
      </MyFormik>
    </Container>
  );
};

export default CreateModeleCourrier;
