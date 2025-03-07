import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import url from "../../module/fetch.tsx";
import DebitSimultane, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./DebitSimultane.tsx";

const UpdateDebitSimultane = ({
  debitSimultaneId,
  typeReseauId,
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: {
  debitSimultaneId: string;
  typeReseauId: string;
  onSubmit: () => void;
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
}) => {
  const { data } = useGet(url`/api/debit-simultane/get/` + debitSimultaneId);

  const { data: listePeiSelectionnable } = useGet(
    url`/api/debit-simultane/pei?${{
      geometry: `SRID=${srid}};POINT(${coordonneeX} ${coordonneeY})`,
      typeReseauId: typeReseauId,
    }}`,
  );

  return (
    data && (
      <MyFormik
        initialValues={getInitialValues(
          data,
          listePeiSelectionnable,
          data.vitesseEau,
          data.siteLibelle,
          data.typeReseauLibelle,
          data.maxDiametreCanalisation,
        )}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/debit-simultane/update/` + debitSimultaneId}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
      >
        <DebitSimultane />
      </MyFormik>
    )
  );
};

export default UpdateDebitSimultane;
