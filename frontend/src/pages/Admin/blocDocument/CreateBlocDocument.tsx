import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import BlocDocument, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./BlocDocument.tsx";

const CreateBlocDocument = () => {
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'un bloc document"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/bloc-document/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_BLOC_DOCUMENT}
      >
        <BlocDocument />
      </MyFormik>
    </Container>
  );
};

export default CreateBlocDocument;
