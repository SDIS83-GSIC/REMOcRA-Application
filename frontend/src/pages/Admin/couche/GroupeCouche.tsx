import { useFormikContext } from "formik";
import { object } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { requiredString } from "../../../module/validators.tsx";

type GroupeCoucheType = {
  groupeCoucheId?: string;
  groupeCoucheCode: string;
  groupeCoucheLibelle: string;
  groupeCoucheProtected: boolean;
};

export const prepareValues = (values: GroupeCoucheType) => ({
  groupeCoucheId: values.groupeCoucheId,
  groupeCoucheCode: values.groupeCoucheCode,
  groupeCoucheLibelle: values.groupeCoucheLibelle,
  groupeCoucheProtected: values.groupeCoucheProtected ?? false,
});

export const groupeCoucheValidationSchema = object({
  groupeCoucheCode: requiredString,
  groupeCoucheLibelle: requiredString,
});

export const getInitialValue = (data?: GroupeCoucheType) => ({
  groupeCoucheId: data?.groupeCoucheId,
  groupeCoucheCode: data?.groupeCoucheCode,
  groupeCoucheLibelle: data?.groupeCoucheLibelle,
  groupeCoucheProtected: data?.groupeCoucheProtected ?? false,
});

export const GroupeCouche = () => {
  const { values }: any = useFormikContext();

  return (
    <FormContainer>
      <TextInput
        name="groupeCoucheCode"
        label="Code"
        required={true}
        disabled={values.groupeCoucheProtected}
      />
      <TextInput name="groupeCoucheLibelle" label="Libellé" required={true} />
      <CheckBoxInput
        name="groupeCoucheProtected"
        label="Protégé"
        disabled={true}
      />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};
