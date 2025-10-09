import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  getInitialEvenementSousCategorieValue,
  prepareValues,
  EvenementSousCategorie,
  evenementSousCategorieValidationSchema,
} from "./EvenementSousCategorie.tsx";

const UpdateEvenementSousCategorie = () => {
  const { evenementSousCategorieId: evenementSousCategorieId } = useParams();
  const evenementSousCategorie = useGet(
    url`/api/evenement-sous-categorie/get/` + evenementSousCategorieId,
  )?.data;
  return (
    evenementSousCategorie && (
      <Container>
        <PageTitle
          title="Mise à jour d'une sous catégorie d'évènement"
          icon={<IconEdit />}
        />
        <MyFormik
          initialValues={getInitialEvenementSousCategorieValue(
            evenementSousCategorie,
          )}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={evenementSousCategorieValidationSchema}
          submitUrl={`/api/evenement-sous-categorie/update/${evenementSousCategorieId}`}
          isPost={false}
          onSubmit={() => true}
          redirectUrl={URLS.LIST_EVENEMENT_SOUS_CATEGORIE}
        >
          <EvenementSousCategorie />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateEvenementSousCategorie;
