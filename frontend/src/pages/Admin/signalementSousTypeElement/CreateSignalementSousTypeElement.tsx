import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import SignalementSousTypeElement, {
  getInitialValue,
  prepareValues,
  validationSchema,
} from "./SignalementSousTypeElement.tsx";

const CreateSignalementSousTypeElement = () => {
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
        submitUrl={`/api/signalement-sous-type-element/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_SIGNALEMENT_SOUS_TYPE_ELEMENT}
        onSubmit={() => true}
      >
        <SignalementSousTypeElement />
      </MyFormik>
    </Container>
  );
};

export default CreateSignalementSousTypeElement;
