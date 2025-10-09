import { Container } from "react-bootstrap";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  prepareValues,
  evenementSousCategorieValidationSchema,
  EvenementSousCategorie,
  getInitialEvenementSousCategorieValue,
} from "./EvenementSousCategorie.tsx";

const CreateEvenementSousCategorie = () => {
  return (
    <Container>
      <PageTitle
        title="Ajouter une sous catégorie d'évènement"
        icon={<IconCreate />}
      />
      <MyFormik
        onSubmit={() => true}
        initialValues={getInitialEvenementSousCategorieValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={evenementSousCategorieValidationSchema}
        submitUrl={`/api/evenement-sous-categorie/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_EVENEMENT_SOUS_CATEGORIE}
      >
        <EvenementSousCategorie />
      </MyFormik>
    </Container>
  );
};

export default CreateEvenementSousCategorie;
