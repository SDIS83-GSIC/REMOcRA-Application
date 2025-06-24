import { Container } from "react-bootstrap";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconCreate } from "../../../../components/Icon/Icon.tsx";
import { URLS } from "../../../../routes.tsx";
import {
  prepareValues,
  TypeCriseCategorie,
  typeCriseCategorieValidationSchema,
} from "./TypeCriseCategorie.tsx";

const CreateTypeCriseCategorie = () => {
  return (
    <Container>
      <PageTitle title="Ajouter un type de catégorie" icon={<IconCreate />} />
      <MyFormik
        initialValues={{}}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={typeCriseCategorieValidationSchema}
        submitUrl={`/api/type-crise-categorie/create/`}
        isPost={true}
        redirectUrl={URLS.LIST_TYPE_CRISE_CATEGORIE}
      >
        <TypeCriseCategorie />
      </MyFormik>
    </Container>
  );
};

export default CreateTypeCriseCategorie;
