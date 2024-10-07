import { useFormikContext } from "formik";
import { Button, Col, Row } from "react-bootstrap";
import { object } from "yup";
import PeiProjetEntity, {
  TypePeiProjet,
} from "../../../Entities/PeiProjetEntity.tsx";
import PositiveNumberInput, {
  FormContainer,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SelectNomenclaturesForm from "../../../components/Form/SelectNomenclaturesForm.tsx";
import TYPE_DATA_CACHE from "../../../enums/NomenclaturesEnum.tsx";
import { requiredString } from "../../../module/validators.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

export const getInitialValues = (data: PeiProjetEntity) => ({
  peiProjetNatureDeciId: data?.peiProjetNatureDeciId,
  peiProjetTypePeiProjet: data?.peiProjetTypePeiProjet,
  peiProjetDiametreId: data?.peiProjetDiametreId,
  peiProjetDiametreCanalisation: data?.peiProjetDiametreCanalisation,
  peiProjetCapacite: data?.peiProjetCapacite,
  peiProjetDebit: data?.peiProjetDebit,
  peiProjetCoordonneeX: data.peiProjetCoordonneeX,
  peiProjetCoordonneeY: data.peiProjetCoordonneeY,
  peiProjetSrid: data.peiProjetSrid,
});

export const validationSchema = object({
  peiProjetNatureDeciId: requiredString,
  peiProjetTypePeiProjet: requiredString,
});
export const prepareVariables = (values: PeiProjetEntity) => ({
  peiProjetNatureDeciId: values.peiProjetNatureDeciId,
  peiProjetTypePeiProjet: values.peiProjetTypePeiProjet,
  peiProjetDiametreId: values.peiProjetDiametreId,
  peiProjetDiametreCanalisation: values.peiProjetDiametreCanalisation,
  peiProjetCapacite: values.peiProjetCapacite,
  peiProjetDebit: values.peiProjetDebit,
  peiProjetCoordonneeX: values.peiProjetCoordonneeX,
  peiProjetCoordonneeY: values.peiProjetCoordonneeY,
  peiProjetSrid: values.peiProjetSrid,
});

const PeiProjet = () => {
  const { values, setValues, setFieldValue }: { values: PeiProjetEntity } =
    useFormikContext();

  const listeTypePeiProjet: IdCodeLibelleType[] = Object.values(
    TypePeiProjet,
  ).map((e) => {
    return {
      id: e,
      code: e,
      libelle: e,
    };
  });

  return (
    <FormContainer>
      <Row>
        <Col>
          <SelectForm
            name={"peiProjetTypePeiProjet"}
            listIdCodeLibelle={listeTypePeiProjet}
            label="Type du PEI en projet"
            defaultValue={listeTypePeiProjet?.find(
              (e) => e.code === values.peiProjetTypePeiProjet,
            )}
            required={true}
            setValues={setValues}
            setOtherValues={() => {
              setFieldValue("peiProjetDiametreId", null);
              setFieldValue("peiProjetDiametreCanalisation", null);
              setFieldValue("peiProjetCapacite", null);
              setFieldValue("peiProjetDebit", null);
            }}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"peiProjetNatureDeciId"}
            nomenclature={TYPE_DATA_CACHE.NATURE_DECI}
            label="Nature DECI"
            valueId={values.peiProjetNatureDeciId}
            required={true}
            setValues={setValues}
          />
        </Col>
      </Row>
      <Row>
        {values.peiProjetTypePeiProjet === TypePeiProjet.PIBI ? (
          <>
            <Col>
              <SelectNomenclaturesForm
                name={"peiProjetDiametreId"}
                nomenclature={TYPE_DATA_CACHE.DIAMETRE}
                label="Diamètre"
                valueId={values.peiProjetDiametreId}
                required={true}
                setValues={setValues}
              />
            </Col>
            <Col>
              <PositiveNumberInput
                name="peiProjetDiametreCanalisation"
                label="Diamètre de canalisation"
                required={true}
              />
            </Col>
          </>
        ) : values.peiProjetTypePeiProjet === TypePeiProjet.RESERVE ? (
          <>
            <Col>
              <PositiveNumberInput
                name="peiProjetCapacite"
                label="Capacité en m³"
                required={true}
              />
            </Col>
            <Col>
              <PositiveNumberInput
                name="peiProjetDebit"
                label="Débit en m³"
                required={true}
              />
            </Col>
          </>
        ) : values.peiProjetTypePeiProjet === TypePeiProjet.PA ? (
          <>
            <PositiveNumberInput
              name="peiProjetDebit"
              label="Débit en m³"
              required={true}
            />
          </>
        ) : (
          ""
        )}
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

export default PeiProjet;
