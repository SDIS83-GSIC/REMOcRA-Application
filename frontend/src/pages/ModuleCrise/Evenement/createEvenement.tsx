import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import { IconEvent } from "../../../components/Icon/Icon.tsx";
import Evenement, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Evenement.tsx";

const CreateEvenement = ({
  criseId,
  state,
  typeEvenement,
  geometrieEvenement,
  onSubmit,
}: CreateEvenementType) => {
  const { user } = useAppContext();
  return (
    <Container>
      <PageTitle
        icon={<IconEvent />}
        title="Nouvel évènement"
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues(
          null,
          geometrieEvenement,
          typeEvenement,
        )}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true} // contient un document
        submitUrl={`/api/crise/${criseId}/evenement/${state}/create`}
        prepareVariables={(values) =>
          prepareVariables(values, null, user.utilisateurId)
        }
        onSubmit={onSubmit}
      >
        <Evenement isReadOnly={false} />
      </MyFormik>
    </Container>
  );
};

type CreateEvenementType = {
  criseId: string;
  typeEvenement: string | undefined;
  geometrieEvenement: string | undefined;
  state: string;

  onSubmit: () => void;
};

export default CreateEvenement;
