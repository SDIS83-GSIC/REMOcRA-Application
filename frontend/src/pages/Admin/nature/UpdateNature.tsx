import { useParams } from "react-router-dom";
import { Container } from "react-bootstrap";
import url from "../../../module/fetch.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import {
  getInitialNatureValue,
  NatureForm,
  natureValidationSchema,
  prepareNatureValues,
} from "./Nature.tsx";

const UpdateNature = () => {
  const { natureId } = useParams();

  const natureState = useGet(url`/api/nature/get/` + natureId);
  return (
    <Container>
      <PageTitle title="Mise à jour d'une nature" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialNatureValue(natureState.data)}
        prepareVariables={(values) => prepareNatureValues(values)}
        validationSchema={natureValidationSchema}
        submitUrl={`/api/nature/update/` + natureId}
        isPost={false}
        redirectUrl={URLS.LIST_NATURE}
        onSubmit={() => true}
      >
        <NatureForm />
      </MyFormik>
    </Container>
  );
};

export default UpdateNature;
