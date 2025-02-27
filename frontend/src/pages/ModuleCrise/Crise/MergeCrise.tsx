import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconMerge } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import CriseMergePage, {
  getInitialValues,
  prepareCriseValues,
  validationSchema,
} from "./CriseMergePage.tsx";

const UpdateCrise = () => {
  const { criseId } = useParams();

  return (
    <Container>
      <PageTitle icon={<IconMerge />} title={"Fusionner la crise"} />
      <MyFormik
        initialValues={getInitialValues(criseId)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/crise/${criseId}/merge`}
        prepareVariables={(values: any) => prepareCriseValues(values)}
        redirectUrl={URLS.LIST_CRISES}
      >
        <CriseMergePage criseId={criseId} />
      </MyFormik>
    </Container>
  );
};

export default UpdateCrise;
