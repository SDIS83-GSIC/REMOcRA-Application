import { Map } from "ol";
import { useParams } from "react-router-dom";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import Loading from "../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { refreshLayerGeoserver } from "../../components/Map/MapUtils.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import Pei, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Pei.tsx";

const UpdatePei = ({
  peiIdUpdate,
  close,
  map,
}: {
  peiIdUpdate?: string;
  close?: () => void;
  map?: Map;
}) => {
  const { peiId } = useParams();

  const id = peiIdUpdate ?? peiId;

  const peiState = useGet(url`/api/pei/` + id);

  const documentsState = useGet(url`/api/documents/pei/` + id);

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
      redirectUrl={!peiIdUpdate ? URLS.PEI : undefined}
      onSubmit={() => {
        if (peiIdUpdate !== undefined) {
          close();
          refreshLayerGeoserver(map);
        }
      }}
    >
      <Pei returnBouton={!peiIdUpdate} close={close} />
    </MyFormik>
  );
};

export default UpdatePei;
