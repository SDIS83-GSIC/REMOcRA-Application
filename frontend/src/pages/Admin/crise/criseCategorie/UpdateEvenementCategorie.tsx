import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  EvenementCategorie,
  evenementCategorieValidationSchema,
  getInitialEvenementCategorieValue,
  prepareValues,
} from "./EvenementCategorie.tsx";

const UpdateEvenementCategorie = () => {
  const { evenementCategorieId: evenementCategorieId } = useParams();
  const evenementCategorie = useGet(
    url`/api/evenement-categorie/get/` + evenementCategorieId,
  )?.data;

  return (
    evenementCategorie && (
      <Container>
        <PageTitle
          title="Mise à jour d'une catégorie d'évènement"
          icon={<IconEdit />}
        />
        <MyFormik
          initialValues={getInitialEvenementCategorieValue(evenementCategorie)}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={evenementCategorieValidationSchema}
          submitUrl={`/api/evenement-categorie/update/${evenementCategorieId}`}
          isPost={false}
          onSubmit={() => true}
          redirectUrl={URLS.LIST_EVENEMENT_CATEGORIE}
        >
          <EvenementCategorie />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateEvenementCategorie;
