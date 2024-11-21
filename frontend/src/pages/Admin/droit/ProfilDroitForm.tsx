import { boolean, object } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { requiredString } from "../../../module/validators.tsx";

export const getInitialValues = (data?: any) => ({
  profilDroitCode: data?.profilDroitCode ?? null,
  profilDroitLibelle: data?.profilDroitLibelle ?? null,
  profilDroitActif: data?.profilDroitActif ?? false,
});

export const prepareValues = (values: any) => ({
  profilDroitCode: values.profilDroitCode,
  profilDroitLibelle: values.profilDroitLibelle,
  profilDroitActif: values.profilDroitActif,
});

export const validationSchema = object({
  profilDroitCode: requiredString,
  profilDroitLibelle: requiredString,
  profilDroitActif: boolean(),
});

const ProfilDroitForm = ({ returnLink }: { returnLink: string }) => {
  return (
    <FormContainer>
      <TextInput label="Libellé" name="profilDroitLibelle" required={true} />
      <TextInput label="Code" name="profilDroitCode" required={true} />
      <CheckBoxInput label="Actif" name="profilDroitActif" />
      <SubmitFormButtons returnLink={returnLink} />
    </FormContainer>
  );
};

export default ProfilDroitForm;
