import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import DocumentHabilitable, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./DocumentHabilitable.tsx";

const CreateDocumentHabilitable = () => {
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'un document"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/document-habilitable/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_DOCUMENT_HABILITABLE}
      >
        <DocumentHabilitable isNew={true} />
      </MyFormik>
    </Container>
  );
};

export default CreateDocumentHabilitable;
