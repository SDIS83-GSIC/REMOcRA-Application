import { Container } from "react-bootstrap";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import { IconEvent } from "../../../components/Icon/Icon.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import Evenement, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Evenement.tsx";

const CreateEvenement = ({
  criseId,
  state,
  evenementSousCategorieId,
  geometrieEvenement,
  onSubmit,
}: CreateEvenementType) => {
  const { user } = useAppContext();
  const sousCategoriesEvenement = useGet(
    url`/api/crise/evenement/get-evenement-sous-categorie${evenementSousCategorieId ? `?evenementSousCategorieId=${evenementSousCategorieId}` : ""}`,
  )?.data;

  return (
    <Container>
      <PageTitle
        icon={<IconEvent />}
        title="Nouvel événement"
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues(
          null,
          geometrieEvenement,
          evenementSousCategorieId,
          sousCategoriesEvenement,
        )}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true} // contient un document
        submitUrl={`/api/crise/${criseId}/evenement/${state}/create`}
        prepareVariables={(values) =>
          prepareVariables(values, null, user!.utilisateurId)
        }
        onSubmit={onSubmit}
      >
        <Evenement
          isReadOnly={false}
          sousCategoriesEvenement={sousCategoriesEvenement}
        />
      </MyFormik>
    </Container>
  );
};

type CreateEvenementType = {
  criseId: string;
  evenementSousCategorieId: string | undefined;
  geometrieEvenement: string | undefined;
  state: string;

  onSubmit: () => void;
};

export default CreateEvenement;
