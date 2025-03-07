import { Button } from "react-bootstrap";
import { useFormikContext } from "formik";
import { object } from "yup";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
} from "../../../components/Form/Form.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import { requiredDate } from "../../../module/validators.tsx";

export const getInitialValues = (criseId: string | undefined) => ({
  criseId: criseId,
  dateDebExtraction: null,
  dateFinExtraction: null,
  hasMessage: null,
  hasDoc: null,
});
export const validationSchema = object({
  dateDebExtraction: requiredDate,
  dateFinExtraction: requiredDate,
});

export const prepareCriseValues = (values: {
  dateDebExtraction: Date;
  dateFinExtraction: Date;
  hasMessage: boolean;
  hasDoc: boolean;
  criseId: string;
}) => ({
  dateDebExtraction: new Date(values.dateDebExtraction).toISOString(),
  dateFinExtraction: new Date(values.dateDebExtraction).toISOString(),
  hasMessage: values.hasMessage,
  hasDoc: values.hasDoc,
  criseId: values.criseId,
});

const CriseExportPage = () => {
  const { values }: { values: any } = useFormikContext();

  return (
    <FormContainer noValidate>
      <h1>Période d&apos;extraction</h1>

      <DateTimeInput
        name="dateDebExtraction"
        label="Extraire les informations entre le"
        required={true}
        value={
          values.dateDebExtraction &&
          formatDateTimeForDateTimeInput(values.dateDebExtraction)
        }
      />

      <DateTimeInput
        name="dateFinExtraction"
        label="et le"
        required={true}
        value={
          values.dateFinExtraction &&
          formatDateTimeForDateTimeInput(values.dateFinExtraction)
        }
      />

      <h1>Export</h1>
      <CheckBoxInput
        name="hasMessage"
        label="Exporter les messages associés aux évènements"
      />
      <CheckBoxInput
        name="hasDoc"
        label="Exporter les documents associés aux évènements"
      />

      <Button type="submit" variant="primary">
        Valider
      </Button>
    </FormContainer>
  );
};

export default CriseExportPage;
