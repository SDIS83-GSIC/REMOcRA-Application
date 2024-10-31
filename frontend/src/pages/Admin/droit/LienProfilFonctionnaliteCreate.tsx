import Container from "react-bootstrap/Container";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import LienProfilFonctionnaliteForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./LienProfilFonctionnaliteForm.tsx";

const ProfilDroitCreate = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'un lien profil / groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/lien-profil-fonctionnalite/create`}
        isPost={true}
        redirectUrl={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}
        onSubmit={() => true}
      >
        <LienProfilFonctionnaliteForm
          returnLink={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}
        />
      </MyFormik>
    </Container>
  );
};

export default ProfilDroitCreate;
