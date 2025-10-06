import { Container } from "react-bootstrap";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  prepareValues,
  evenementSousCategorieValidationSchema,
  EvenementSousCategorie,
} from "./TypeCriseCategorie.tsx";

const CreateEvenementSousCategorie = () => {
  return (
    <Container>
      <PageTitle
        title="Ajouter une sous catégorie d'évènement"
        icon={<IconCreate />}
      />
      <MyFormik
        initialValues={{}}
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
