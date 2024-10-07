import { Container } from "react-bootstrap";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { URLS } from "../../../routes.tsx";
import {
  OrganismeForm,
  organismeValidationSchema,
  prepareOrganismeValues,
} from "./Organisme.tsx";

const CreateOrganisme = () => {
  return (
    <Container>
      <PageTitle title="Ajouter un organisme" icon={<IconEdit />} />
      <MyFormik
        initialValues={{
          organismeActif: true,
          organismeCode: null,
          organismeLibelle: null,
          organismeEmailContact: null,
          organismeProfilOrganismeId: null,
          organismeTypeOrganismeId: null,
          organismeZoneIntegrationId: null,
          organismeParentId: null,
        }}
        prepareVariables={(values) => prepareOrganismeValues(values)}
        validationSchema={organismeValidationSchema}
        submitUrl={`/api/organisme/create/`}
        isPost={true}
        redirectUrl={URLS.ORGANISME}
        onSubmit={() => true}
      >
        <OrganismeForm />
      </MyFormik>
    </Container>
  );
};

export default CreateOrganisme;
