import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import PeiProjet, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./PeiProjet.tsx";

const UpdatePeiProjet = () => {
  const { etudeId, peiProjetId } = useParams();

  const peiProjetState = useGet(
    url`/api/couverture-hydraulique/pei-projet/` + peiProjetId,
  );

  return (
    <Container>
      <PageTitle icon={<IconPei />} title="Modification d'un PEI en projet" />
      <MyFormik
        initialValues={getInitialValues(peiProjetState?.data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={
          `/api/couverture-hydraulique/etude/` +
          etudeId +
          `/pei-projet/` +
          peiProjetId
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

export default UpdatePeiProjet;
