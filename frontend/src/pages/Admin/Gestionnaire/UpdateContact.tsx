import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Contact, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Contact.tsx";

const UpdateContact = () => {
  const { gestionnaireId, contactId } = useParams();
  const { data } = useGet(url`/api/contact/get/` + contactId);
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'un contact"} />
      <MyFormik
        initialValues={getInitialValues(data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/contact/` + gestionnaireId + `/update/` + contactId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_CONTACT(gestionnaireId)}
      >
        <Contact />
      </MyFormik>
    </Container>
  );
};

export default UpdateContact;
