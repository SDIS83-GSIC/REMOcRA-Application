import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import Utilisateur, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Utilisateur.tsx";

const CreateUtilisateur = () => {
  return (
    <Container>
      <PageTitle icon={<IconCreate />} title={"Création d'un utilisateur"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={false}
        submitUrl={`/api/utilisateur/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_UTILISATEUR}
      >
        <Utilisateur />
      </MyFormik>
    </Container>
  );
};

export default CreateUtilisateur;
