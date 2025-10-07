import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import CreateLayerMetadataForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CreateLayerMetadataForm.tsx";

const CreateLayerMetadata = () => {
  return (
    <Container>
      <PageTitle title="Ajout des métadonnées" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche/add-style`}
        isPost={true}
        redirectUrl={URLS.URL_LIST_LAYER_STYLE}
        onSubmit={() => true}
      >
        <CreateLayerMetadataForm />
      </MyFormik>
    </Container>
  );
};

export default CreateLayerMetadata;
