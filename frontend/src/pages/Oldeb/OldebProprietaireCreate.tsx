import Container from "react-bootstrap/Container";
import { URLS } from "../../routes.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconProprietaire } from "../../components/Icon/Icon.tsx";
import OldebProprietaireForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./OldebProprietaireForm.tsx";

const OldebProprietaireCreate = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconProprietaire />}
        title={"Création d'un propriétaire"}
      />
      <MyFormik
        initialValues={getInitialValues}
        validationSchema={validationSchema}
        isPost={true}
        submitUrl={`/api/proprietaire/create`}
        prepareVariables={(values) => prepareValues(values)}
        redirectUrl={URLS.OLDEB_PROPRIETAIRE_LIST}
      >
        <OldebProprietaireForm
          isNew={true}
          returnLink={URLS.OLDEB_PROPRIETAIRE_LIST}
        />
      </MyFormik>
    </Container>
  );
};

export default OldebProprietaireCreate;
