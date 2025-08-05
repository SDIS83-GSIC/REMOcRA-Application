import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Etude, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Etude.tsx";

const UpdateEtude = () => {
  const { etudeId } = useParams();

  const etudeState = useGet(url`/api/couverture-hydraulique/etude/` + etudeId);
  return (
    <Container>
      <PageTitle icon={<IconEtude />} title={"Mise à jour d'une étude"} />
      <MyFormik
        initialValues={getInitialValues(etudeState?.data)}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/couverture-hydraulique/etude/` + etudeId}
        prepareVariables={(values) =>
          prepareVariables(values, etudeState?.data)
        }
        redirectUrl={URLS.LIST_ETUDE}
        onSubmit={() => {}}
      >
        <Etude />
      </MyFormik>
    </Container>
  );
};

export default UpdateEtude;
