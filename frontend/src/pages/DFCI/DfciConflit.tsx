import { useFormikContext } from "formik";
import { Col, Row } from "react-bootstrap";
import { object } from "yup";
import {
  DateInput,
  FormContainer,
  TextInput,
} from "../../components/Form/Form.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { DfciConflitEntity } from "../../Entities/DfciConflitEntity.tsx";
import { requiredString } from "../../module/validators.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";

export const getInitialValuesDfciConflit = (data: DfciConflitEntity) => ({
  dfciConflitId: data?.dfciConflitId,
  dfciConflitTable: data?.dfciConflitTable,
  dfciConflitElementId: data?.dfciConflitElementId,
  dfciConflitChamp: data?.dfciConflitChamp,
  dfciConflitValeurRemocra: data?.dfciConflitValeurRemocra,
  dfciConflitValeurSig: data?.dfciConflitValeurSig,
  dfciConflitDate: formatForDateInput(data?.dfciConflitDate ?? new Date()),
});

export const prepareVariablesDfciConflit = (values: DfciConflitEntity) => ({
  dfciConflitId: values.dfciConflitId,
  dfciConflitTable: values.dfciConflitTable,
  dfciConflitElementId: values.dfciConflitElementId,
  dfciConflitChamp: values.dfciConflitChamp,
  dfciConflitValeurRemocra: values.dfciConflitValeurRemocra,
  dfciConflitValeurSig: values.dfciConflitValeurSig,
  dfciConflitDate: values.dfciConflitDate
    ? new Date(values.dfciConflitDate).toISOString()
    : null,
});

export const validationSchemaDfciConflit = object({
  dfciConflitElementId: requiredString,
  dfciConflitChamp: requiredString,
});

export const DfciConflit = () => {
  const { values, setFieldValue } = useFormikContext<DfciConflitEntity>();
  return (
    <FormContainer>
      <Row>
        <TextInput
          name="dfciConflitTable"
          label="Table en conflit"
          value={values.dfciConflitTable}
          disabled={true}
          required={false}
        />
      </Row>
      <Row>
        <TextInput
          name="dfciConflitElementId"
          label="ID de l'élément en conflit"
          value={values.dfciConflitElementId}
          disabled={true}
          required={false}
        />
      </Row>
      <Row>
        <DateInput
          name="dfciConflitDate"
          label="Date du conflit"
          readOnly={true}
          value={formatForDateInput(values.dfciConflitDate)}
          required={false}
        />
      </Row>
      <Row>
        <TextInput
          name="dfciConflitChamp"
          label="Champ en conflit"
          value={values.dfciConflitChamp}
          disabled={true}
          required={false}
        />
      </Row>
      <Row>
        <Col>
          <Row>
            <TextInput
              name="dfciConflitValeurRemocra"
              label="Valeur de REMOcRA"
              value={values.dfciConflitValeurRemocra}
              disabled={true}
              required={false}
            />
          </Row>
          <Row>
            <SubmitFormButtons
              onClick={() => {
                setFieldValue(
                  "dfciConflitValeurSig",
                  values.dfciConflitValeurRemocra,
                );
              }}
              submitTitle="Choisir la valeur de REMOcRA"
            />
          </Row>
        </Col>
        <Col>
          <Row>
            <TextInput
              name="dfciConflitValeurSig"
              label="Valeur du SIG"
              value={values.dfciConflitValeurSig}
              disabled={true}
              required={false}
            />
          </Row>
          <Row>
            <SubmitFormButtons
              onClick={() => {
                setFieldValue(
                  "dfciConflitValeurRemocra",
                  values.dfciConflitValeurSig,
                );
              }}
              submitTitle="Choisir la valeur du SIG"
            />
          </Row>
        </Col>
      </Row>
    </FormContainer>
  );
};
