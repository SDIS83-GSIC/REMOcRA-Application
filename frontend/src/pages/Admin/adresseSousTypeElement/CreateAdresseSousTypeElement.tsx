import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import AdresseSousTypeElement, {
  getInitialValue,
  prepareValues,
  validationSchema,
} from "./AdresseSousTypeElement.tsx";

const CreateAdresseSousTypeElement = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'un sous-type élement"
        icon={<IconCreate />}
      />
      <MyFormik
        initialValues={getInitialValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/adresse-sous-type-element/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_ADRESSE_SOUS_TYPE_ELEMENT}
        onSubmit={() => true}
      >
        <AdresseSousTypeElement />
      </MyFormik>
    </Container>
  );
};

export default CreateAdresseSousTypeElement;
