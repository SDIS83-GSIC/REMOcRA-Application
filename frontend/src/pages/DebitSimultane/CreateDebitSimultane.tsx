import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import url from "../../module/fetch.tsx";
import DebitSimultane, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./DebitSimultane.tsx";

const CreateDebitSimultane = ({
  typeReseauId,
  listePibiId,
  onSubmit,
}: {
  typeReseauId: string;
  onSubmit: () => void;
  listePibiId: string[];
}) => {
  const { data: vitesseEau } = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(PARAMETRE.VITESSE_EAU),
    }}`,
  );

  const { data: listePeiSelectionnable } = useGet(
    url`/api/debit-simultane/pei?${{
      listePibiId: JSON.stringify(listePibiId),
      typeReseauId: typeReseauId,
    }}`,
  );

  const { data } = useGet(
    url`/api/debit-simultane/get-infos?${{
      listePibiId: JSON.stringify(listePibiId),
    }}`,
  );

  return (
    data && (
      <MyFormik
        initialValues={getInitialValues(
          {
            listeDebitSimultaneMesure: [
              {
                debitSimultaneMesureDateMesure: new Date(),
                listePeiId: listePibiId,
              },
            ],
          },
          listePeiSelectionnable,
          vitesseEau,
          data.siteLibelle,
          data.typeReseauLibelle,
          data.pibiDiametreCanalisation,
        )}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/debit-simultane/create/`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
      >
        <DebitSimultane />
      </MyFormik>
    )
  );
};

export default CreateDebitSimultane;
