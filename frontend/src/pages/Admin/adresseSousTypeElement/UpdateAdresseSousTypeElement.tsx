import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import AdresseSousTypeElement, {
  getInitialValue,
  prepareValues,
  validationSchema,
} from "./AdresseSousTypeElement.tsx";

const UpdateAdresseSousTypeElement = () => {
  const { adresseSousTypeElementId } = useParams();
  const data = useGet(
    url`/api/adresse-sous-type-element/get/${adresseSousTypeElementId}`,
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
        submitUrl={`/api/adresse-sous-type-element/update/${adresseSousTypeElementId}`}
        isPost={false}
        redirectUrl={URLS.LIST_ADRESSE_SOUS_TYPE_ELEMENT}
        onSubmit={() => true}
      >
        <AdresseSousTypeElement />
      </MyFormik>
    </Container>
  );
};

export default UpdateAdresseSousTypeElement;
