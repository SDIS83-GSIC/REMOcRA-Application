import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import { IconEvent } from "../../../components/Icon/Icon.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import Evenement, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Evenement.tsx";

const UpdateEvenement = ({
  state,
  readOnly,
  criseId,
  evenementId,
  geometrieEvenement,
  onSubmit,
}: {
  state: string;
  readOnly: boolean;
  criseId: string;
  evenementId: string;
  geometrieEvenement: string | undefined;
  onSubmit: () => void;
}) => {
  const selectDataState = useGet(url`/api/crise/evenement/${evenementId}`);
  const sousCategorieList = useGet(
    url`/api/crise/evenement/get-evenement-sous-categorie${selectDataState.data?.evenementSousCategorieId ? `?evenementSousCategorieId=${selectDataState.data?.evenementSousCategorieId}` : ""}`,
  )?.data;
  const { user } = useAppContext();

  return (
    selectDataState.data && (
      <Container>
        <PageTitle
          icon={<IconEvent />}
          title={readOnly ? "Informations" : "Modifier l'événement"}
          displayReturnButton={false}
        />
        <MyFormik
          initialValues={getInitialValues(
            selectDataState.data,
            geometrieEvenement,
            selectDataState.data?.evenementSousCategorieId,
            sousCategorieList,
          )}
          validationSchema={validationSchema}
          isPost={false}
          isMultipartFormData={true} // contient un document
          prepareVariables={(values) =>
            prepareVariables(values, selectDataState.data, user.utilisateurId)
          }
          onSubmit={onSubmit}
          submitUrl={`/api/crise/${criseId}/evenement/${state}/${evenementId}/update`}
        >
          <Evenement
            isReadOnly={readOnly}
            sousCategoriesEvenement={sousCategorieList}
          />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateEvenement;
