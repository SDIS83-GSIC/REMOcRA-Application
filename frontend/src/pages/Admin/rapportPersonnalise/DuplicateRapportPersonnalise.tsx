import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconDuplicate } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import RapportPersonnalise, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./RapportPersonnalise.tsx";

const DuplicateRapportPersonnalise = () => {
  const { rapportPersonnaliseId } = useParams();
  const { data } = useGet(
    url`/api/rapport-personnalise/get/` + rapportPersonnaliseId,
  );
  return (
    data && (
      <Container>
        <PageTitle
          title={"Duplication d'un rapport personnalisé"}
          icon={<IconDuplicate />}
        />
        <MyFormik
          initialValues={getInitialValues({
            ...data,
            rapportPersonnaliseCode: data.rapportPersonnaliseCode + "_COPY",
            rapportPersonnaliseLibelle:
              data.rapportPersonnaliseLibelle + " (copie)",
            rapportPersonnaliseProtected: false,
          })}
          prepareVariables={(values) => prepareVariables(values)}
          validationSchema={validationSchema}
          submitUrl={`/api/rapport-personnalise/create/`}
          isPost={true}
          redirectUrl={URLS.LIST_RAPPORT_PERSONNALISE}
          onSubmit={() => true}
        >
          <RapportPersonnalise />
        </MyFormik>
      </Container>
    )
  );
};

export default DuplicateRapportPersonnalise;
