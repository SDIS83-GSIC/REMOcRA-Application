import { Container } from "react-bootstrap";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  EvenementCategorie,
  evenementCategorieValidationSchema,
  getInitialEvenementCategorieValue,
  prepareValues,
} from "./EvenementCategorie.tsx";

const CreateEvenementCategorie = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'une catégorie d'évènement"
        icon={<IconCreate />}
      />
      <MyFormik
        onSubmit={() => true}
        initialValues={getInitialEvenementCategorieValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={evenementCategorieValidationSchema}
        submitUrl={`/api/evenement-categorie/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_EVENEMENT_CATEGORIE}
      >
        <EvenementCategorie />
      </MyFormik>
    </Container>
  );
};

export default CreateEvenementCategorie;
