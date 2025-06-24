import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  getInitialTypeCriseCategorieValue,
  prepareValues,
  TypeCriseCategorie,
  typeCriseCategorieValidationSchema,
} from "./TypeCriseCategorie.tsx";

const UpdateTypeCriseCategorie = () => {
  const { typeCriseCategorieId } = useParams();

  const typeCriseCategorieState = useGet(
    url`/api/type-crise-categorie/get/` + typeCriseCategorieId,
  );
  return (
    typeCriseCategorieState.data && (
      <Container>
        <PageTitle
          title="Mise à jour d'un type de catégorie de crise"
          icon={<IconEdit />}
        />
        <MyFormik
          initialValues={getInitialTypeCriseCategorieValue(
            typeCriseCategorieState.data,
          )}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={typeCriseCategorieValidationSchema}
          submitUrl={`/api/type-crise-categorie/update/` + typeCriseCategorieId}
          isPost={false}
          redirectUrl={URLS.LIST_TYPE_CRISE_CATEGORIE}
          onSubmit={() => true}
        >
          <TypeCriseCategorie />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateTypeCriseCategorie;
