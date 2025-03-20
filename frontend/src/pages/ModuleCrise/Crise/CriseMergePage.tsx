import { Button } from "react-bootstrap";
import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  DateTimeInput,
  FormContainer,
  Multiselect,
} from "../../../components/Form/Form.tsx";
import { CriseType } from "../../../Entities/CriseEntity.tsx";
import url from "../../../module/fetch.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import { requiredDate } from "../../../module/validators.tsx";

export const getInitialValues = (criseId: string | undefined) => ({
  criseId: criseId,
  criseDateFin: new Date(),
});
export const validationSchema = object({
  criseDateFin: requiredDate,
});

export const prepareCriseValues = (values: {
  criseDateFin: Date;
  listeCriseId: string[];
  criseId: string;
}) => ({
  criseDateFin: new Date(values.criseDateFin ?? new Date()),
  listeCriseId: values.listeCriseId,
  criseId: values.criseId,
});

const CriseMergePage = ({ criseId }: { criseId: string | undefined }) => {
  const { setFieldValue, values }: { values: CriseType } = useFormikContext();
  const listeCriseFusion = useGet(url`/api/crise/getCriseForMerge`)?.data;
  const index = listeCriseFusion?.findIndex(
    (element: { criseId: string }) => element.criseId === criseId,
  );
  if (index !== -1) {
    listeCriseFusion?.splice(index, 1);
  }

  return (
    <FormContainer noValidate>
      <Multiselect
        options={listeCriseFusion}
        name={"listeCrises"}
        label="Liste des crises à fusionner"
        getOptionValue={(t) => t.criseId}
        getOptionLabel={(t) => t.criseLibelle}
        isClearable={true}
        onChange={(crise) => {
          const criseId = crise.map((e: { criseId: any }) => e.criseId);
          criseId.length > 0
            ? setFieldValue("listeCriseId", criseId)
            : setFieldValue("listeCriseId", undefined);
        }}
      />

      <DateTimeInput
        name="criseDateFin"
        label="Date et heure de fusion"
        required={true}
        value={
          values.criseDateFin &&
          formatDateTimeForDateTimeInput(values.criseDateFin)
        }
      />

      <Button type="submit" variant="primary">
        Valider
      </Button>
    </FormContainer>
  );
};

export default CriseMergePage;
