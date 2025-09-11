import { boolean, object } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { requiredString } from "../../../module/validators.tsx";

export const getInitialValues = (data?: any) => ({
  groupeFonctionnalitesCode: data?.groupeFonctionnalitesCode ?? null,
  groupeFonctionnalitesLibelle: data?.groupeFonctionnalitesLibelle ?? null,
  groupeFonctionnalitesActif: data?.groupeFonctionnalitesActif ?? false,
});

export const prepareValues = (values: any) => ({
  groupeFonctionnalitesCode: values.groupeFonctionnalitesCode,
  groupeFonctionnalitesLibelle: values.groupeFonctionnalitesLibelle,
  groupeFonctionnalitesActif: values.groupeFonctionnalitesActif,
});

export const validationSchema = object({
  groupeFonctionnalitesCode: requiredString,
  groupeFonctionnalitesLibelle: requiredString,
  groupeFonctionnalitesActif: boolean(),
});

const GroupeFonctionnalitesForm = () => {
  return (
    <FormContainer>
      <TextInput
        label="Libellé"
        name="groupeFonctionnalitesLibelle"
        required={true}
      />
      <TextInput
        label="Code"
        name="groupeFonctionnalitesCode"
        required={true}
      />
      <CheckBoxInput label="Actif" name="groupeFonctionnalitesActif" />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default GroupeFonctionnalitesForm;
