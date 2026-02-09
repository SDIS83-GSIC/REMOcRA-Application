import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import IndisponibiliteTemporaireForm, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./IndisponibiliteTemporaireForm.tsx";

const CreateIndisponibiliteTemporaire = ({
  listePeiId,
  onSubmit,
}: {
  listePeiId?: string[];
  onSubmit?: () => void;
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
      {...(listePeiId
        ? { onSubmit: onSubmit }
        : { redirectUrl: URLS.LIST_INDISPONIBILITE_TEMPORAIRE })}
    >
      <IndisponibiliteTemporaireForm
        title={"Nouvelle indisponibilité temporaire"}
        listePeiId={listePeiId}
      />
    </MyFormik>
  );
};

export default CreateIndisponibiliteTemporaire;
