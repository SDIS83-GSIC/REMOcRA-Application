import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import TourneeForm, {
  getInitialValues,
  prepareVariables,
} from "./TourneeForm.tsx";

const CreateTournee = () => {
  return (
    <MyFormik
      initialValues={getInitialValues}
      isPost={true}
      submitUrl={`/api/tournee/createTournee`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={URLS.TOURNEE}
    >
      <TourneeForm isCreation={true} />
    </MyFormik>
  );
};

export default CreateTournee;
