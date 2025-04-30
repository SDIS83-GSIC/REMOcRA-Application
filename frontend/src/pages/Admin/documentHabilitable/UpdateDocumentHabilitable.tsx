import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import DocumentHabilitable, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./DocumentHabilitable.tsx";

const UpdateDocumentHabilitable = () => {
  const { documentHabilitableId } = useParams();
  const { data } = useGet(
    url`/api/document-habilitable/get/` + documentHabilitableId,
  );
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'un document"} />
      <MyFormik
        initialValues={getInitialValues(data)}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/document-habilitable/update/` + documentHabilitableId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_DOCUMENT_HABILITABLE}
      >
        <DocumentHabilitable />
      </MyFormik>
    </Container>
  );
};

export default UpdateDocumentHabilitable;
