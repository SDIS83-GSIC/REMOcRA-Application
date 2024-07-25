import { useParams } from "react-router-dom";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import Pei, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Pei.tsx";

const UpdatePei = () => {
  const { peiId } = useParams();

  const peiState = useGet(url`/api/pei/` + peiId);

  const documentsState = useGet(url`/api/documents/pei/` + peiId);

  if (!peiState.isResolved) {
    return <Loading />;
  }
  const { data }: { data: PeiEntity } = peiState;

  data.documents = documentsState.data;

  return (
    <MyFormik
      initialValues={getInitialValues(data)}
      validationSchema={validationSchema}
      isPost={false}
      isMultipartFormData={true}
      submitUrl={`/api/pei/update`}
      prepareVariables={(values) => prepareVariables(values, data)}
      redirectUrl={URLS.PEI}
    >
      <Pei />
    </MyFormik>
  );
};

export default UpdatePei;
