import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import PeiProjet, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./PeiProjet.tsx";

const CreatePeiProjet = ({
  coordonneeX,
  coordonneeY,
  srid,
  etudeId,
  onSubmit,
}: CreatePeiProjetType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconPei />}
        title="Création d'un PEI en projet"
        displayReturnButton={false}
      />
      <MyFormik
        initialValues={getInitialValues({
          peiProjetCoordonneeX: coordonneeX,
          peiProjetCoordonneeY: coordonneeY,
          peiProjetSrid: srid,
          peiProjetEtudeId: etudeId,
        })}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={false}
        submitUrl={
          `/api/couverture-hydraulique/etude/` + etudeId + `/pei-projet/create`
        }
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
      >
        <PeiProjet />
      </MyFormik>
    </Container>
  );
};

type CreatePeiProjetType = {
  // Coordonnées du points saisies sur la carte
  coordonneeX: number;
  coordonneeY: number;
  srid: string;

  // Id de l'étude
  etudeId: string;

  onSubmit: () => void;
};

export default CreatePeiProjet;
