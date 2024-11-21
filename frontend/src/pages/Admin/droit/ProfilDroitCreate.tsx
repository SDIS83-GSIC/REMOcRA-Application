import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import ProfilDroitForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./ProfilDroitForm.tsx";

const ProfilDroitCreate = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'un groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/profil-droit/create`}
        isPost={true}
        redirectUrl={URLS.PROFIL_DROIT_LIST}
        onSubmit={() => true}
      >
        <ProfilDroitForm returnLink={URLS.PROFIL_DROIT_LIST} />
      </MyFormik>
    </Container>
  );
};

export default ProfilDroitCreate;
