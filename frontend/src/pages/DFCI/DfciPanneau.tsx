import { useFormikContext } from "formik";
import { Row } from "react-bootstrap";
import { object, string } from "yup";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateInput,
  FormContainer,
  TextAreaInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import {
  DfciPanneauEntity,
  listTypeBzero,
  listTypeEquipement,
  listTypePanneau,
  listTypePostion,
} from "../../Entities/DfciPanneauEntity.tsx";
import url from "../../module/fetch.tsx";
import { requiredBoolean, requiredDate } from "../../module/validators.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValuesPanneau = (data: DfciPanneauEntity) => ({
  dfciPanneauId: data?.dfciPanneauId,
  dfciPanneauType: data?.dfciPanneauType,
  dfciPanneauEtat: data?.dfciPanneauEtat ?? false,
  dfciPanneauBzero: data?.dfciPanneauBzero,
  dfciPanneauDateGps: formatForDateInput(
    data?.dfciPanneauDateGps ?? new Date(),
  ),
  dfciPanneauPosition: data?.dfciPanneauPosition,
  dfciPanneauEquipement: data?.dfciPanneauEquipement,
  dfciPanneauDfciPisteId: data?.dfciPanneauDfciPisteId,
  dfciPanneauNumPiste: data?.dfciPanneauNumPiste ?? false,
  dfciPanneauLibellePiste: data?.dfciPanneauLibellePiste ?? false,
  dfciPanneauRemarque: data?.dfciPanneauRemarque,
  dfciPanneauGeometrie: data?.dfciPanneauGeometrie,
  dfciPanneauCode: data?.dfciPanneauCode,
  dfciPanneauVersion: data?.dfciPanneauVersion,
});

export const prepareVariablesPanneau = (values: DfciPanneauEntity) => ({
  dfciPanneauId: values.dfciPanneauId,
  dfciPanneauType: values.dfciPanneauType,
  dfciPanneauEtat: values.dfciPanneauEtat,
  dfciPanneauBzero: values.dfciPanneauBzero,
  dfciPanneauDateGps: values.dfciPanneauDateGps
    ? new Date(values.dfciPanneauDateGps).toISOString()
    : null,
  dfciPanneauPosition: values.dfciPanneauPosition,
  dfciPanneauEquipement: values.dfciPanneauEquipement,
  dfciPanneauDfciPisteId: values.dfciPanneauDfciPisteId,
  dfciPanneauNumPiste: values.dfciPanneauNumPiste,
  dfciPanneauLibellePiste: values.dfciPanneauLibellePiste,
  dfciPanneauRemarque: values.dfciPanneauRemarque ?? null,
  dfciPanneauGeometrie: values.dfciPanneauGeometrie,
  dfciPanneauCode: values.dfciPanneauCode,
  dfciPanneauVersion: values.dfciPanneauVersion,
});

export const validationSchemaPanneau = object({
  dfciPanneauType: string(),
  dfciPanneauEtat: requiredBoolean,
  dfciPanneauBzero: string(),
  dfciPanneauDateGps: requiredDate,
  dfciPanneauPosition: string(),
  dfciPanneauEquipement: string(),
  dfciPanneauDfciPisteId: string(),
  dfciPanneauNumPiste: requiredBoolean,
  dfciPanneauLibellePiste: requiredBoolean,
  dfciPanneauRemarque: string().nullable(),
});

const DfciPanneau = ({ readOnly }: { readOnly: boolean }) => {
  const { values, setFieldValue } = useFormikContext<DfciPanneauEntity>();

  const listPiste: IdCodeLibelleType[] = useGet(
    url`/api/dfci-pistes/piste-id-code-libelle?${{ geometrie: values.dfciPanneauGeometrie }}`,
  ).data;

  return (
    <>
      {listPiste && (
        <FormContainer>
          <Row>
            <SelectForm
              name="dfciPanneauType"
              listIdCodeLibelle={listTypePanneau}
              label="Type de panneau"
              defaultValue={
                values.dfciPanneauType
                  ? listTypePanneau.find((e) => e.id === values.dfciPanneauType)
                  : undefined
              }
              disabled={readOnly}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPanneauEtat"
              label="En bon état"
              disabled={readOnly}
              checked={values.dfciPanneauEtat}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPanneauBzero"
              listIdCodeLibelle={listTypeBzero}
              label="Type B0"
              defaultValue={
                values.dfciPanneauBzero
                  ? listTypeBzero.find((e) => e.id === values.dfciPanneauBzero)
                  : undefined
              }
              disabled={readOnly}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <DateInput
              name="dfciPanneauDateGps"
              label="Date GPS"
              readOnly={true}
              value={values.dfciPanneauDateGps}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPanneauPosition"
              listIdCodeLibelle={listTypePostion}
              label="Position"
              defaultValue={
                values.dfciPanneauPosition
                  ? listTypePostion.find(
                      (e) => e.id === values.dfciPanneauPosition,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPanneauEquipement"
              listIdCodeLibelle={listTypeEquipement}
              label="Equipement"
              defaultValue={
                values.dfciPanneauEquipement
                  ? listTypeEquipement.find(
                      (e) => e.id === values.dfciPanneauEquipement,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPanneauDfciPisteId"
              listIdCodeLibelle={listPiste}
              label="Piste"
              defaultValue={
                values.dfciPanneauDfciPisteId
                  ? listPiste.find(
                      (e) => e.id === values.dfciPanneauDfciPisteId,
                    )
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPanneauNumPiste"
              label="Présence du n° de piste"
              disabled={readOnly}
              checked={values.dfciPanneauNumPiste}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPanneauLibellePiste"
              label="Présence du libellé de piste"
              disabled={readOnly}
              checked={values.dfciPanneauLibellePiste}
              required={true}
            />
          </Row>
          <Row>
            <TextAreaInput
              name="dfciPanneauRemarque"
              label="Remarque"
              disabled={readOnly}
              value={values.dfciPanneauRemarque}
              required={false}
            />
          </Row>
          {!readOnly && <SubmitFormButtons />}
        </FormContainer>
      )}
    </>
  );
};
export default DfciPanneau;
