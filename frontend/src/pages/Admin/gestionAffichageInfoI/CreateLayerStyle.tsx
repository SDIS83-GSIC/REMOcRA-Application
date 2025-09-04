import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import CreateLayerStyleForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CreateLayerStyleForm.tsx";

const CreateLayerStyle = () => {
  return (
    <Container>
      <PageTitle title="Création d'un style" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/admin/couche/add-style`}
        isPost={true}
        redirectUrl={URLS.URL_LIST_LAYER_STYLE}
        onSubmit={() => true}
      >
        <CreateLayerStyleForm />
      </MyFormik>
    </Container>
  );
};

export default CreateLayerStyle;
