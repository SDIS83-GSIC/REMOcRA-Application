import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { URLS } from "../../../routes.tsx";
import {
  NatureForm,
  natureValidationSchema,
  prepareNatureValues,
} from "./Nature.tsx";

const CreateNature = () => {
  return (
    <Container>
      <PageTitle title="Ajouter une nature" icon={<IconEdit />} />
      <MyFormik
        initialValues={{
          natureActif: true,
          natureCode: "",
          natureLibelle: "",
          natureTypePei: "",
          natureProtected: false,
        }}
        prepareVariables={(values) => prepareNatureValues(values)}
        validationSchema={natureValidationSchema}
        submitUrl={`/api/nature/add/`}
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
