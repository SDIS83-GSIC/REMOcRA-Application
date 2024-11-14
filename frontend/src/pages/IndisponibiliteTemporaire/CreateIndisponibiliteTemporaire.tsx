import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import IndisponibiliteTemporaireForm, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./IndisponibiliteTemporaireForm.tsx";

const CreateIndisponibiliteTemporaire = ({
  listePeiId,
}: {
  listePeiId?: string[];
}) => {
  return (
    <MyFormik
      initialValues={getInitialValues({
        indisponibiliteTemporaireListePeiId: listePeiId,
      })}
      validationSchema={validationSchema}
      isPost={true}
      submitUrl={`/api/indisponibilite-temporaire/create`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={URLS.LIST_INDISPONIBILITE_TEMPORAIRE}
    >
      <IndisponibiliteTemporaireForm
        title={"Nouvelle indisponibilité temporaire"}
      />
    </MyFormik>
  );
};

export default CreateIndisponibiliteTemporaire;
