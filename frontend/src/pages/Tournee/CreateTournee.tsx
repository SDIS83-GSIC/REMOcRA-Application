import { useNavigate } from "react-router-dom";
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
  const navigate = useNavigate();

  return (
    <MyFormik
      initialValues={getInitialValues()}
      isPost={true}
      submitUrl={`/api/tournee/createTournee`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={undefined}
      onSubmit={(e) =>
        !isFromMap
          ? navigate(URLS.TOURNEE_PEI(e.tourneeId))
          : setTourneeId && setTourneeId(e.tourneeId)
      }
    >
      <TourneeForm isCreation={true} isFromMap={isFromMap} />
    </MyFormik>
  );
};

export default CreateTournee;
