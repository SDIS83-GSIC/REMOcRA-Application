import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import AnomalieForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./AnomalieForm.tsx";

const AnomalieUpdate = () => {
  const { anomalieId } = useParams();

  const anomalieState = useGet(url`/api/anomalie/${anomalieId}`);

  if (!anomalieState.isResolved) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle title="Modification d'une anomalie" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValues(anomalieState.data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/anomalie/update/${anomalieId}`}
        isPost={false}
        redirectUrl={URLS.LIST_ANOMALIE}
        onSubmit={() => true}
      >
        <AnomalieForm returnLink={URLS.LIST_ANOMALIE} />
      </MyFormik>
    </Container>
  );
};

export default AnomalieUpdate;
