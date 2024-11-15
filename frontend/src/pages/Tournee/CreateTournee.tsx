import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import TourneeForm, {
  getInitialValues,
  prepareVariables,
} from "./TourneeForm.tsx";

const CreateTournee = ({
  isFromMap = false,
  setTourneeId,
}: {
  isFromMap?: boolean;
  setTourneeId: (e: string) => void;
}) => {
  return (
    <MyFormik
      initialValues={getInitialValues()}
      isPost={true}
      submitUrl={`/api/tournee/createTournee`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={!isFromMap ? URLS.LIST_TOURNEE : undefined}
      onSubmit={(e) => setTourneeId && setTourneeId(e.tourneeId)}
    >
      <TourneeForm isCreation={true} isFromMap={isFromMap} />
    </MyFormik>
  );
};

export default CreateTournee;
