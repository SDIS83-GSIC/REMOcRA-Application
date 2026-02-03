import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import {
  getInitialOrganismeValue,
  OrganismeForm,
  organismeValidationSchema,
  prepareOrganismeValues,
} from "./Organisme.tsx";

const UpdateOrganisme = () => {
  const { organismeId } = useParams();
  const organismeState = useGet(url`/api/organisme/get/` + organismeId);

  return (
    <Container>
      <PageTitle title="Modification d'un organisme" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialOrganismeValue(organismeState.data)}
        prepareVariables={(values) => prepareOrganismeValues(values)}
        validationSchema={organismeValidationSchema}
        submitUrl={`/api/organisme/update/` + organismeId}
        isPost={false}
        redirectUrl={URLS.LIST_ORGANISME}
        onSubmit={() => true}
      >
        <OrganismeForm />
      </MyFormik>
    </Container>
  );
};

export default UpdateOrganisme;
