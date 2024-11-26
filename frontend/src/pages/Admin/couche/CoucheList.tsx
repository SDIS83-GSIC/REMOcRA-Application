import { Container } from "react-bootstrap";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconLayers } from "../../../components/Icon/Icon.tsx";
import CoucheForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CoucheForm.tsx";

const CoucheList = () => {
  const layerState = useGet(url`/api/admin/couche`);

  if (!layerState.isResolved) {
    return <Loading />;
  }

  const formData = layerState.data;

  return (
    <Container>
      <PageTitle title="Gestion des couches" icon={<IconLayers />} />
      <MyFormik
        initialValues={getInitialValues(formData)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche`}
        isMultipartFormData={true}
        isPost={false}
        onSubmit={() => true}
      >
        <CoucheForm />
      </MyFormik>
    </Container>
  );
};

export default CoucheList;
