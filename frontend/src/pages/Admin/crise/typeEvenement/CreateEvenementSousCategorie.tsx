import { Container } from "react-bootstrap";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import SousTypeCreateForm, {
  getInitialValues,
  sousTypeValidationSchema,
  prepareSousTypeValues,
} from "./SousTypeCreateForm.tsx";

const CreateEvenementSousCategorie = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconCreate />}
        title={"Ajout d'une sous catégorie d'évènement"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={sousTypeValidationSchema}
        isPost={true}
        onSubmit={() => true}
        submitUrl={`/api/evenement-sous-categorie/create`}
        prepareVariables={(values) => prepareSousTypeValues(values)}
        redirectUrl={URLS.LIST_EVENEMENT_SOUS_CATEGORIE}
      >
        <SousTypeCreateForm />
      </MyFormik>
    </Container>
  );
};

export default CreateEvenementSousCategorie;
