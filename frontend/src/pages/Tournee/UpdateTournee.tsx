import { useParams } from "react-router-dom";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { URLS } from "../../routes.tsx";
import { TourneeEntity } from "../../Entities/TourneeEntity.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import TourneeForm, {
  getInitialValues,
  prepareVariables,
} from "./TourneeForm.tsx";

const UpdateTournee = () => {
  const { tourneeId } = useParams();
  const tourneeInfo = useGet(url`/api/tournee/` + tourneeId);

  if (!tourneeInfo.isResolved) {
    return <Loading />;
  }

  const { data }: { data: TourneeEntity } = tourneeInfo;

  return (
    <MyFormik
      initialValues={getInitialValues(data)}
      isPost={false}
      isMultipartFormData={false}
      submitUrl={`/api/tournee/updateTournee`}
      prepareVariables={(values) => prepareVariables(values)}
      redirectUrl={URLS.LIST_TOURNEE}
    >
      <TourneeForm tourneeLibelle={data.tourneeLibelle} />
    </MyFormik>
  );
};

export default UpdateTournee;
