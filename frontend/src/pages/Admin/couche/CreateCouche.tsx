import { Container } from "react-bootstrap";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconMapComponent } from "../../../components/Icon/Icon.tsx";
import { navigateGoBack } from "../../../utils/fonctionsUtils.tsx";
import Couche, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./Couche.tsx";

const CreateCouche = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { groupeCoucheId } = useParams<{ groupeCoucheId: string }>();
  return (
    <Container>
      <PageTitle title="Création d'une couche" icon={<IconMapComponent />} />
      <MyFormik
        initialValues={getInitialValues(groupeCoucheId!)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche/groupe-couche/${groupeCoucheId}/create`}
        isPost={true}
        onSubmit={() => navigateGoBack(location, navigate)}
        isMultipartFormData={true}
      >
        <Couche />
      </MyFormik>
    </Container>
  );
};

export default CreateCouche;
