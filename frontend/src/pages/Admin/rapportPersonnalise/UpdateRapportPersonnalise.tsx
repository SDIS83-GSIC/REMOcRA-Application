import { useMemo } from "react";
import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import RapportPersonnalise, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./RapportPersonnalise.tsx";

const UpdateRapportPersonnalise = () => {
  const { rapportPersonnaliseId } = useParams();
  const { data } = useGet(
    url`/api/rapport-personnalise/get/` + rapportPersonnaliseId,
  );
  const initialValues = useMemo(() => getInitialValues(data), [data]);
  return (
    <Container>
      <PageTitle
        title={"Modification d'un rapport personnalisé"}
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={initialValues}
        prepareVariables={(values) => prepareVariables(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/rapport-personnalise/update/` + rapportPersonnaliseId}
        isPost={false}
        redirectUrl={URLS.LIST_RAPPORT_PERSONNALISE}
        onSubmit={() => true}
      >
        <RapportPersonnalise />
      </MyFormik>
    </Container>
  );
};

export default UpdateRapportPersonnalise;
