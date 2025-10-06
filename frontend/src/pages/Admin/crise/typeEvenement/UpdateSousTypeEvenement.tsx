import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../../components/Icon/Icon.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import SousTypeCreateForm, {
  getInitialValues,
  prepareSousTypeValues,
  sousTypeValidationSchema,
} from "./SousTypeCreateForm.tsx";

const UpdateSousTypeEvenement = () => {
  const { sousTypeId } = useParams();
  const sousTypeState = useGet(
    url`/api/type-crise-categorie/get/${sousTypeId!}`,
  );
  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour du sous type"} />
      <MyFormik
        initialValues={getInitialValues(sousTypeState?.data)}
        validationSchema={sousTypeValidationSchema}
        isPost={false}
        onSubmit={() => true}
        submitUrl={`/api/type-crise-categorie/update/${sousTypeId}`}
        prepareVariables={(values) => prepareSousTypeValues(values)}
        redirectUrl={URLS.LIST_SOUS_TYPE_EVENEMENT}
      >
        <SousTypeCreateForm />
      </MyFormik>
    </Container>
  );
};

export default UpdateSousTypeEvenement;
