import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Crise, {
  getInitialValues,
  prepareCriseValues,
  criseValidationSchema,
} from "./Crise.tsx";

const UpdateCrise = () => {
  const { criseId } = useParams();
  const criseState = useGet(url`/api/crise/${criseId}`);
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'une crise"} />
      <MyFormik
        initialValues={getInitialValues(criseState?.data)}
        validationSchema={criseValidationSchema}
        isPost={false}
        submitUrl={`/api/crise/${criseId}/update`}
        prepareVariables={(values) => prepareCriseValues(values)}
        redirectUrl={URLS.LIST_CRISES}
      >
        <Crise />
      </MyFormik>
    </Container>
  );
};

export default UpdateCrise;
