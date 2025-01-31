import { useLocation } from "react-router-dom";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import Pei, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Pei.tsx";

const CreatePei = () => {
  // En cas de création via la carte, on récupère les coordonnées passées dans le state
  const { state } = useLocation();
  let initialValues;
  if (state) {
    initialValues = state;
    // On vide le state
    window.history.replaceState({ from: state.from }, "");
  }
  return (
    <MyFormik
      initialValues={getInitialValues(initialValues)}
      validationSchema={validationSchema}
      isPost={true}
      isMultipartFormData={true}
      submitUrl={`/api/pei/create/`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={URLS.PEI}
    >
      <Pei isNew={true} />
    </MyFormik>
  );
};

export default CreatePei;
