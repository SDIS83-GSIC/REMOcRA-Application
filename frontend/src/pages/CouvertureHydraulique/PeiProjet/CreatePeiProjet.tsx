import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import PeiProjet, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./PeiProjet.tsx";

const CreatePeiProjet = () => {
  const { etudeId } = useParams();
  return (
    <Container>
      <PageTitle icon={<IconPei />} title="Création d'un PEI en projet" />
      <MyFormik
        initialValues={getInitialValues(null)}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={false}
        submitUrl={
          `/api/couverture-hydraulique/etude/` + etudeId + `/pei-projet/create`
        }
        prepareVariables={(values) => prepareVariables(values)}
        // TODO redirect vers la carte
        redirectUrl={URLS.PEI}
      >
        <PeiProjet />
      </MyFormik>
    </Container>
  );
};

export default CreatePeiProjet;
