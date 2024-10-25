import { object } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { URLS } from "../../../routes.tsx";

type GestionnaireType = {
  gestionnaireCode: string;
  gestionnaireLibelle: string;
  gestionnaireActif: boolean;
};

export const getInitialValues = (data: GestionnaireType) => ({
  gestionnaireCode: data?.gestionnaireCode ?? null,
  gestionnaireLibelle: data?.gestionnaireLibelle ?? null,
  gestionnaireActif: data?.gestionnaireActif ?? null,
});

export const validationSchema = object({});

export const prepareVariables = (values: GestionnaireType) => ({
  gestionnaireCode: values.gestionnaireCode,
  gestionnaireLibelle: values.gestionnaireLibelle,
  gestionnaireActif: values.gestionnaireActif,
});

const Gestionnaire = () => {
  return (
    <FormContainer>
      <TextInput label="Code" name="gestionnaireCode" required={true} />
      <TextInput label="Libellé" name="gestionnaireLibelle" required={true} />
      <CheckBoxInput name="gestionnaireActif" label="Actif" />
      <SubmitFormButtons returnLink={URLS.LIST_GESTIONNAIRE} />
    </FormContainer>
  );
};

export default Gestionnaire;
