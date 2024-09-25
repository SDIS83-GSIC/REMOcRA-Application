import { useParams } from "react-router-dom";
import { Container } from "react-bootstrap";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { URLS } from "../../../routes.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import {
  DiametreForm,
  getInitialDiametreValue,
  prepareDiametreValues,
  diametreValidationSchema,
} from "./Diametre.tsx";

const UpdateDiametre = () => {
  const { diametreId } = useParams();

  const diametreState = useGet(url`/api/diametre/` + diametreId);

  return (
    <Container>
      <PageTitle title="Modification d'un diamètre" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialDiametreValue(diametreState.data)}
        prepareVariables={(values) => prepareDiametreValues(values)}
        validationSchema={diametreValidationSchema}
        submitUrl={`/api/diametre/update/` + diametreId}
        isPost={false}
        redirectUrl={URLS.DIAMETRE}
        onSubmit={() => true}
      >
        <DiametreForm />
      </MyFormik>
    </Container>
  );
};

export default UpdateDiametre;
