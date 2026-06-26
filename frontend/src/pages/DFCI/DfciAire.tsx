import { useFormikContext } from "formik";
import { Row } from "react-bootstrap";
import { object, string } from "yup";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateInput,
  FormContainer,
  NumberInput,
  TextAreaInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { hasDroit } from "../../droits.tsx";
import {
  DfciAireEntity,
  listTypeAire,
} from "../../Entities/DfciAireEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import url from "../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredDate,
  requiredString,
} from "../../module/validators.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValuesDfciAire = (data: DfciAireEntity) => ({
  dfciAireId: data?.dfciAireId,
  dfciAireAmenagement: data?.dfciAireAmenagement ?? false,
  dfciAireDateGps: formatForDateInput(data?.dfciAireDateGps ?? new Date()),
  dfciAireGrandeDimension: data?.dfciAireGrandeDimension,
  dfciAirePetiteDimension: data?.dfciAirePetiteDimension,
  dfciAireType: data?.dfciAireType,
  dfciAireGeometrie: data?.dfciAireGeometrie,
  dfciAireDfciPisteId: data?.dfciAireDfciPisteId,
  dfciAireRemarque: data?.dfciAireRemarque,
  dfciAireCode: data?.dfciAireCode,
  dfciAireVersion: data?.dfciAireVersion,
});

export const prepareVariablesDfciAire = (values: DfciAireEntity) => ({
  dfciAireId: values.dfciAireId,
  dfciAireAmenagement: values.dfciAireAmenagement,
  dfciAireDateGps: values.dfciAireDateGps
    ? new Date(values.dfciAireDateGps).toISOString()
    : null,
  dfciAireGrandeDimension: values.dfciAireGrandeDimension,
  dfciAirePetiteDimension: values.dfciAirePetiteDimension,
  dfciAireType: values.dfciAireType,
  dfciAireGeometrie: values.dfciAireGeometrie,
  dfciAireDfciPisteId: values.dfciAireDfciPisteId ?? null,
  dfciAireRemarque: values.dfciAireRemarque ?? null,
  dfciAireCode: values.dfciAireCode,
  dfciAireVersion: values.dfciAireVersion,
});

export const validationSchemaDfciAire = object({
  dfciAireAmenagement: requiredBoolean,
  dfciAireDateGps: requiredDate,
  dfciAireType: requiredString,
  dfciAireDfciPisteId: string().nullable(),
});

const DfciAire = ({ readOnly }: { readOnly: boolean }) => {
  const { user } = useAppContext();
  const { values, setFieldValue } = useFormikContext<DfciAireEntity>();

  const listPiste: IdCodeLibelleType[] = useGet(
    url`/api/dfci-pistes/piste-id-code-libelle?${{ geometrie: values.dfciAireGeometrie }}`,
  ).data;

  const hasDroitUpdateDfciAire = hasDroit(user, TYPE_DROIT.DFCI_AIRE_U);
  const isDisabled = readOnly || !hasDroitUpdateDfciAire;

  return (
    <>
      {listPiste && (
        <FormContainer>
          {!hasDroitUpdateDfciAire && !readOnly && (
            <p className="fade alert alert-danger show">
              Vous n'avez pas le droit de modification
            </p>
          )}
          <Row>
            <CheckBoxInput
              name="dfciAireAmenagement"
              label="Aménagement"
              disabled={isDisabled}
              checked={values.dfciAireAmenagement}
              required={true}
            />
          </Row>
          <Row>
            <DateInput
              name="dfciAireDateGps"
              label="Date GPS"
              readOnly={true}
              value={values.dfciAireDateGps}
              required={true}
            />
          </Row>
          <Row>
            <NumberInput
              name="dfciAireGrandeDimension"
              label="Plus grande dimension (en mètres)"
              disabled={isDisabled}
              value={values.dfciAireGrandeDimension}
              min={0}
              step={0.1}
              required={false}
            />
          </Row>
          <Row>
            <NumberInput
              name="dfciAirePetiteDimension"
              label="Plus petite dimension (en mètres)"
              disabled={isDisabled}
              value={values.dfciAirePetiteDimension}
              min={0}
              step={0.1}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciAireType"
              listIdCodeLibelle={listTypeAire}
              label="Type d'aire"
              defaultValue={listTypeAire?.find(
                (e) => e.code === values.dfciAireType,
              )}
              disabled={isDisabled}
              setFieldValue={setFieldValue}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciAireDfciPisteId"
              listIdCodeLibelle={listPiste}
              label="Piste"
              defaultValue={
                values.dfciAireDfciPisteId
                  ? listPiste.find((e) => e.id === values.dfciAireDfciPisteId)
                  : undefined
              }
              disabled={true}
            />
          </Row>
          <Row>
            <TextAreaInput
              name="dfciAireRemarque"
              label="Remarque"
              value={values.dfciAireRemarque}
              required={false}
              disabled={isDisabled}
            />
          </Row>
          {!readOnly && hasDroitUpdateDfciAire && <SubmitFormButtons />}
        </FormContainer>
      )}
    </>
  );
};

export default DfciAire;
