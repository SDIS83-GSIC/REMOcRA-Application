import { useFormikContext } from "formik";
import { degreesToStringHDMS } from "ol/coordinate";
import { transform } from "ol/proj";
import { Button, Col, Row } from "react-bootstrap";
import PositiveNumberInput, {
  DateInput,
  FormContainer,
  FormLabel,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import PeiPrescritEntity from "../../Entities/PeiPrescritEntity.tsx";
import { EPSG_3857, EPSG_4326 } from "../../utils/constantsUtils.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";

export const getInitialValues = (data: PeiPrescritEntity) => ({
  peiPrescritId: data?.peiPrescritId,
  peiPrescritDate: formatForDateInput(data?.peiPrescritDate ?? new Date()),
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
  const coordinates = transform(
    [values.peiPrescritCoordonneeX, values.peiPrescritCoordonneeY],
    EPSG_3857,
    EPSG_4326,
  );

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
        <DateInput
          name="peiPrescritDate"
          label="Date"
          required={false}
          value={values.peiPrescritDate}
        />
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
            <p>
              {degreesToStringHDMS("NS", coordinates[1], 4) +
                " " +
                degreesToStringHDMS("EO", coordinates[0], 4)}
            </p>
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
