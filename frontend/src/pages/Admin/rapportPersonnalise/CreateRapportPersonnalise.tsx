import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import RapportPersonnalise, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./RapportPersonnalise.tsx";

const CreateRapportPersonnalise = () => {
  return (
    <Container>
      <PageTitle
        title={"Création d'un rapport personnalisé"}
        icon={<IconCreate />}
      />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareVariables(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/rapport-personnalise/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_RAPPORT_PERSONNALISE}
        onSubmit={() => true}
      >
        <RapportPersonnalise />
      </MyFormik>
    </Container>
  );
};

export default CreateRapportPersonnalise;
