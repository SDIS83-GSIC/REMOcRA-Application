import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import {
  getInitialNatureValue,
  NatureForm,
  natureValidationSchema,
  prepareNatureValues,
} from "./Nature.tsx";

const CreateNature = () => {
  return (
    <Container>
      <PageTitle title="Ajouter une nature" icon={<IconCreate />} />
      <MyFormik
        initialValues={getInitialNatureValue()}
        prepareVariables={(values) => prepareNatureValues(values)}
        validationSchema={natureValidationSchema}
        submitUrl={`/api/nature/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_NATURE}
        onSubmit={() => true}
      >
        <NatureForm />
      </MyFormik>
    </Container>
  );
};

export default CreateNature;
