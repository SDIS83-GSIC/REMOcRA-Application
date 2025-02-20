import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import ModeleCourrier, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./ModeleCourrier.tsx";

const UpdateModeleCourrier = () => {
  const { modeleCourrierId } = useParams();
  const { data } = useGet(
    url`/api/courriers/modele-courrier/get/${modeleCourrierId}`,
  );
  return (
    <Container>
      <PageTitle
        title={"Modification d'un modèle de courrier"}
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues(data)}
        prepareVariables={(values) => prepareVariables(values, data)}
        validationSchema={validationSchema}
        submitUrl={`/api/courriers/modeles/update/${modeleCourrierId}`}
        isPost={false}
        isMultipartFormData={true}
        redirectUrl={URLS.LIST_MODELE_COURRIER}
      >
        <ModeleCourrier />
      </MyFormik>
    </Container>
  );
};

export default UpdateModeleCourrier;
