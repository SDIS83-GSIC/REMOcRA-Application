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
  typeEvenementCategorieValidationSchema,
} from "./TypeCriseCategorie.tsx";

const UpdateEvenementSousCategorie = () => {
  const { typeEvenementCategorieId: evenementSousCategorieId } = useParams();

  const evenementSousCategorieState = useGet(
    url`/api/evenement_sous_categorie/get/` + evenementSousCategorieId,
  );
  return (
    evenementSousCategorieState.data && (
      <Container>
        <PageTitle
          title="Mise à jour d'un type de catégorie d'évènement"
          icon={<IconEdit />}
        />
        <MyFormik
          initialValues={getInitialEvenementSousCategorieValue(
            evenementSousCategorieState.data,
          )}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={typeEvenementCategorieValidationSchema}
          submitUrl={
            `/api/evenement_sous_categorie/update/` + evenementSousCategorieId
          }
          isPost={false}
          redirectUrl={URLS.LIST_EVENEMENT_SOUS_CATEGORIE}
          onSubmit={() => true}
        >
          <EvenementSousCategorie />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateEvenementSousCategorie;
