import { Container } from "react-bootstrap";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconMapComponent } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { navigateGoBack } from "../../../utils/fonctionsUtils.tsx";
import Couche, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./Couche.tsx";

const UpdateCouche = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { groupeCoucheId, coucheId } = useParams<{
    groupeCoucheId: string;
    coucheId: string;
  }>();
  const { data } = useGet(url`/api/admin/couche/${coucheId!}`);
  return (
    <Container>
      <PageTitle
        title="Modification d'un de couche"
        icon={<IconMapComponent />}
      />
      <MyFormik
        initialValues={getInitialValues(groupeCoucheId!, data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche/groupe-couche/${groupeCoucheId}/update/${coucheId}`}
        isPost={false}
        onSubmit={() => navigateGoBack(location, navigate)}
        isMultipartFormData={true}
      >
        <Couche />
      </MyFormik>
    </Container>
  );
};

export default UpdateCouche;
