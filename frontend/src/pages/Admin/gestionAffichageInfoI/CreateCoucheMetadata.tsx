import Container from "react-bootstrap/Container";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import CoucheMetadataForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CoucheMetadataForm.tsx";

const CreateCoucheMetadata = () => {
  return (
    <Container>
      <PageTitle title="Ajout des métadonnées" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche-metadata/add-couche-metadata`}
        isPost={true}
        redirectUrl={URLS.LIST_COUCHE_METADATA}
        onSubmit={() => true}
      >
        <CoucheMetadataForm />
      </MyFormik>
    </Container>
  );
};

export default CreateCoucheMetadata;
