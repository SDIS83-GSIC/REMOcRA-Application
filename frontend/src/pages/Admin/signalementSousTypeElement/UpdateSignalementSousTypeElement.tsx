import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import SignalementSousTypeElement, {
  getInitialValue,
  prepareValues,
  validationSchema,
} from "./SignalementSousTypeElement.tsx";

const UpdateSignalementSousTypeElement = () => {
  const { signalementSousTypeElementId } = useParams();
  const data = useGet(
    url`/api/signalement-sous-type-element/get/${signalementSousTypeElementId}`,
  ).data;

  return (
    <Container>
      <PageTitle
        title="Modification d'un sous-type élement"
        icon={<IconCreate />}
      />
      <MyFormik
        initialValues={getInitialValue(data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/signalement-sous-type-element/update/${signalementSousTypeElementId}`}
        isPost={false}
        redirectUrl={URLS.LIST_SIGNALEMENT_SOUS_TYPE_ELEMENT}
        onSubmit={() => true}
      >
        <SignalementSousTypeElement />
      </MyFormik>
    </Container>
  );
};

export default UpdateSignalementSousTypeElement;
