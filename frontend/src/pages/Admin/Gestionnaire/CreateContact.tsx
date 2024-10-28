import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import Contact, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Contact.tsx";

const CreateContact = () => {
  const { gestionnaireId } = useParams();
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'un contact"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/contact/` + gestionnaireId + `/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_GESTIONNAIRE}
      >
        <Contact />
      </MyFormik>
    </Container>
  );
};

export default CreateContact;
