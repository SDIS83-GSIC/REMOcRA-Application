import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import BlocDocument, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./BlocDocument.tsx";

const UpdateBlocDocument = () => {
  const { blocDocumentId } = useParams();
  const { data } = useGet(url`/api/bloc-document/get/` + blocDocumentId);
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'un bloc document"} />
      <MyFormik
        initialValues={getInitialValues(data)}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/bloc-document/update/` + blocDocumentId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_BLOC_DOCUMENT}
      >
        <BlocDocument />
      </MyFormik>
    </Container>
  );
};

export default UpdateBlocDocument;
