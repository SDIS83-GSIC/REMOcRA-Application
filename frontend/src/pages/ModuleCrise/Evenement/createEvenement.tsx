import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEvent } from "../../../components/Icon/Icon.tsx";
import Evenement, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Evenement.tsx";

const CreateEvenement = ({
  criseId,
  typeEvenement,
  geometrieEvenement,
  onSubmit,
}: CreateEvenementType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconEvent />}
        title="Nouvel évenement"
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues(
          null,
          geometrieEvenement,
          typeEvenement,
        )} // remplir avec les géométries (voir createPeiProjet)
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true} // contient un document
        submitUrl={`/api/crise/` + criseId + `/evenement/create`}
        prepareVariables={(values) => prepareVariables(values, null)}
        onSubmit={onSubmit}
      >
        <Evenement />
      </MyFormik>
    </Container>
  );
};

type CreateEvenementType = {
  // Id de la crise associée
  criseId: string;
  typeEvenement: string | undefined;
  geometrieEvenement: string | undefined;

  onSubmit: () => void;
};

export default CreateEvenement;
