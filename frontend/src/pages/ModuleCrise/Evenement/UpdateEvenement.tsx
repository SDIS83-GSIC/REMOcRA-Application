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
  readOnly,
  criseId,
  evenementId,
  geometrieEvenement,
  onSubmit,
}: {
  readOnly: boolean;
  criseId: string;
  evenementId: string;
  geometrieEvenement: string | undefined;
  onSubmit: () => void;
}) => {
  const selectDataState = useGet(
    url`/api/crise/evenement/getEvenement/${evenementId}`,
  );
  const { user } = useAppContext();
  const title = readOnly ? "Informations" : "Modifier l'événement";

  return (
    selectDataState && (
      <Container>
        <PageTitle
          icon={<IconEvent />}
          title={title}
          displayReturnButton={false}
        />
        <MyFormik
          initialValues={getInitialValues(
            selectDataState.data,
            geometrieEvenement,
          )}
          validationSchema={validationSchema}
          isPost={false}
          isMultipartFormData={true} // contient un document
          prepareVariables={(values) =>
            prepareVariables(values, selectDataState.data, user.utilisateurId)
          }
          onSubmit={onSubmit}
          submitUrl={`/api/crise/${criseId}/evenement/${evenementId}/update`}
        >
          <Evenement isReadOnly={readOnly} />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateEvenement;
