import { Map } from "ol";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { refreshLayerGeoserver } from "../../components/Map/MapUtils.tsx";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import Pei, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Pei.tsx";

const CreatePei = ({
  coordonneesPeiCreate,
  close,
  map,
}: {
  coordonneesPeiCreate: {
    coordonneeX: string;
    coordonneY: string;
    srid: number;
  };
  close: () => void;
  map: Map;
}) => {
  const initialValues = getInitialValues({
    coordonneeX: coordonneesPeiCreate.coordonneeX,
    coordonneeY: coordonneesPeiCreate.coordonneeY,
    srid: parseInt(coordonneesPeiCreate.srid),
  } as PeiEntity);

  return (
    <>
      <MyFormik
        initialValues={initialValues}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/pei/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={() => {
          close();
          refreshLayerGeoserver(map);
        }}
      >
        <Pei isNew={true} close={close} returnBouton={false} />
      </MyFormik>
    </>
  );
};

export default CreatePei;
