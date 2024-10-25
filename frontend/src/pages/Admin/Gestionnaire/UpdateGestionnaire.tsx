import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Gestionnaire, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Gestionnaire.tsx";

const UpdateGestionnaire = () => {
  const { gestionnaireId } = useParams();

  const gestionnaireState = useGet(url`/api/gestionnaire/` + gestionnaireId);
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'un gestionnaire"} />
      <MyFormik
        initialValues={getInitialValues(gestionnaireState?.data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/gestionnaire/update/` + gestionnaireId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_GESTIONNAIRE}
      >
        <Gestionnaire />
      </MyFormik>
    </Container>
  );
};

export default UpdateGestionnaire;
