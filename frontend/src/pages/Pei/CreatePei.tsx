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
  const location = useLocation();
  const state = location.state ?? {};
  const {
    coordonneeX: coordonneeX = null,
    coordonneeY: coordonneeY = null,
    srid: srid = null,
    ...rest
  } = state;
  const initialValues = {
    ...getInitialValues(),
    ...{
      coordonneeX,
      coordonneeY,
      srid,
    },
  };

  if (state) {
    window.history.replaceState(rest, "");
  }

  return (
    <MyFormik
      initialValues={initialValues}
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
