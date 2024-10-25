import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import AnomalieForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./AnomalieForm.tsx";

const AnomalieCreate = () => {
  return (
    <Container>
      <PageTitle title="Création d'une anomalie" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/anomalie/create`}
        isPost={true}
        redirectUrl={URLS.ANOMALIE}
        onSubmit={() => true}
      >
        <AnomalieForm returnLink={URLS.ANOMALIE} />
      </MyFormik>
    </Container>
  );
};

export default AnomalieCreate;
