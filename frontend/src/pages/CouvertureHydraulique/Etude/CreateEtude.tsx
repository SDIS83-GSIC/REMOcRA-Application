import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import Etude, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Etude.tsx";

const CreateEtude = () => {
  return (
    <Container>
      <PageTitle icon={<IconEtude />} title={"Création d'une étude"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/couverture-hydraulique/etude/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_ETUDE}
        onSubmit={() => {}}
      >
        <Etude />
      </MyFormik>
    </Container>
  );
};

export default CreateEtude;
