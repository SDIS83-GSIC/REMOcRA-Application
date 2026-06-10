import { useParams } from "react-router-dom";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import IndisponibiliteTemporaireForm, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./IndisponibiliteTemporaireForm.tsx";

const UpdateIndisponibiliteTemporaire = () => {
  const { indisponibiliteTemporaireId } = useParams();
  const indisponibiliteTemporaireState = useGet(
    url`/api/indisponibilite-temporaire/` + indisponibiliteTemporaireId,
  );
  return (
    <MyFormik
      initialValues={getInitialValues({
        ...indisponibiliteTemporaireState?.data,
        listePeiId:
          indisponibiliteTemporaireState?.data
            ?.indisponibiliteTemporaireListePeiId,
      })}
      validationSchema={validationSchema}
      submitUrl={
        `/api/indisponibilite-temporaire/` + indisponibiliteTemporaireId
      }
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={URLS.LIST_INDISPONIBILITE_TEMPORAIRE}
    >
      <IndisponibiliteTemporaireForm
        title={"Modifier une indisponibilité temporaire"}
      />
    </MyFormik>
  );
};

export default UpdateIndisponibiliteTemporaire;
