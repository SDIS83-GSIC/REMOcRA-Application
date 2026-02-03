import { forwardRef, useImperativeHandle, useRef } from "react";
import { useNavigate } from "react-router-dom";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import TourneeForm, {
  getInitialValues,
  prepareVariables,
} from "./TourneeForm.tsx";

const CreateTournee = forwardRef(
  (
    {
      isFromMap = false,
      setTourneeId,
      listePei = [],
    }: {
      isFromMap?: boolean;
      setTourneeId: (e: string) => void;
      listePei?: string[];
    },
    ref,
  ) => {
    const navigate = useNavigate();
    const formikRef = useRef<any>();

    useImperativeHandle(ref, () => ({
      submit: () => {
        if (formikRef.current) {
          formikRef.current.submitForm();
        }
      },
    }));

    return (
      <MyFormik
        innerRef={formikRef}
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
        <TourneeForm
          isCreation={true}
          listePei={listePei}
          hideSubmit={isFromMap}
        />
      </MyFormik>
    );
  },
);

CreateTournee.displayName = "CreateTournee";

export default CreateTournee;
