import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import Pei, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Pei.tsx";

const CreatePei = () => {
  return (
    <MyFormik
      initialValues={getInitialValues()}
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
