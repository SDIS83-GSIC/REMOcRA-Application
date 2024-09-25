import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { URLS } from "../../../routes.tsx";
import {
  DiametreForm,
  prepareDiametreValues,
  diametreValidationSchema,
} from "./Diametre.tsx";

const CreateDiametre = () => {
  return (
    <Container>
      <PageTitle title="Création d'un diamètre" icon={<IconCreate />} />
      <MyFormik
        initialValues={{
          diametreCode: "",
          diametreLibelle: "",
          diametreActif: true,
          diametreProtected: false,
        }}
        prepareVariables={(values) => prepareDiametreValues(values)}
        validationSchema={diametreValidationSchema}
        submitUrl={`/api/diametre/create/`}
        isPost={true}
        redirectUrl={URLS.DIAMETRE}
        onSubmit={() => true}
      >
        <DiametreForm />
      </MyFormik>
    </Container>
  );
};

export default CreateDiametre;
