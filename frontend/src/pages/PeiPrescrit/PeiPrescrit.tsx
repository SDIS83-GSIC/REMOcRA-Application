import { useFormikContext } from "formik";
import { Button, Col, Row } from "react-bootstrap";
import PositiveNumberInput, {
  DateInput,
  FormContainer,
  FormLabel,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import PeiPrescritEntity from "../../Entities/PeiPrescritEntity.tsx";

export const getInitialValues = (data: PeiPrescritEntity) => ({
  peiPrescritId: data?.peiPrescritId,
  peiPrescritDate: data?.peiPrescritDate,
  peiPrescritDebit: data?.peiPrescritDebit,
  peiPrescritNbPoteaux: data?.peiPrescritNbPoteaux,
  peiPrescritCommentaire: data?.peiPrescritCommentaire,
  peiPrescritAgent: data?.peiPrescritAgent,
  peiPrescritNumDossier: data?.peiPrescritNumDossier,
  peiPrescritCoordonneeX: data?.peiPrescritCoordonneeX,
  peiPrescritCoordonneeY: data?.peiPrescritCoordonneeY,
  peiPrescritSrid: data?.peiPrescritSrid,
});

export const prepareVariables = (values: PeiPrescritEntity) => ({
  peiPrescritId: values.peiPrescritId,
  peiPrescritDate: values.peiPrescritDate
    ? new Date(values.peiPrescritDate).toISOString()
    : null,
  peiPrescritDebit: values.peiPrescritDebit,
  peiPrescritNbPoteaux: values.peiPrescritNbPoteaux,
  peiPrescritCommentaire: values.peiPrescritCommentaire,
  peiPrescritAgent: values.peiPrescritAgent,
  peiPrescritNumDossier: values.peiPrescritNumDossier,
  peiPrescritGeometrie: `SRID=${values.peiPrescritSrid};POINT(${values.peiPrescritCoordonneeX} ${values.peiPrescritCoordonneeY})`,
});

const PeiPrescrit = () => {
  const { values }: { values: any } = useFormikContext();

  return (
    <FormContainer>
      <Row>
        <PositiveNumberInput
          name="peiPrescritNbPoteaux"
          label="Nombre de PEI"
          required={false}
        />
      </Row>
      <Row>
        <PositiveNumberInput
          name="peiPrescritDebit"
          label="Débit (m³/h) : "
          min={0}
          required={false}
        />
      </Row>
      <Row>
        <DateInput name="peiPrescritDate" label="Date" required={false} />
      </Row>
      <Row>
        <TextInput name="peiPrescritAgent" label="Agent" required={false} />
      </Row>
      <Row>
        <TextInput
          name="peiPrescritNumDossier"
          label="Numéro de dossier"
          required={false}
        />
      </Row>
      <Row>
        <TextAreaInput
          name="peiPrescritCommentaire"
          label="Commentaire"
          required={false}
        />
      </Row>
      <Row>
        <Row>
          <FormLabel name="" label="Coordonnées WGS" required={false} />
        </Row>
        <Row>
          <Col>
            <p>Long : {values.peiPrescritCoordonneeX}</p>
          </Col>
          <Col>
            <p>Lat : {values.peiPrescritCoordonneeY}</p>
          </Col>
        </Row>
      </Row>
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Valider
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

export default PeiPrescrit;
