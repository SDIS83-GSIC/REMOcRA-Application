import { object } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { URLS } from "../../../routes.tsx";

type ZoneIntegrationType = {
  zoneIntegrationCode: string;
  zoneIntegrationLibelle: string;
  zoneIntegrationActif: boolean;
};

export const getInitialValues = (data: ZoneIntegrationType) => ({
  zoneIntegrationCode: data?.zoneIntegrationCode ?? null,
  zoneIntegrationLibelle: data?.zoneIntegrationLibelle ?? null,
  zoneIntegrationActif: data?.zoneIntegrationActif ?? false,
});

export const validationSchema = object({});

export const prepareVariables = (values: ZoneIntegrationType) => ({
  zoneIntegrationCode: values.zoneIntegrationCode,
  zoneIntegrationLibelle: values.zoneIntegrationLibelle,
  zoneIntegrationActif: values.zoneIntegrationActif,
});

const ZoneIntegration = () => {
  return (
    <FormContainer>
      <h3 className="mt-1">Informations générales</h3>
      <TextInput label="Code" name="zoneIntegrationCode" required={true} />
      <TextInput
        label="Libellé"
        name="zoneIntegrationLibelle"
        required={true}
      />
      <CheckBoxInput name="zoneIntegrationActif" label="Actif" />
      <SubmitFormButtons returnLink={URLS.LIST_ZONE_INTEGRATION} />
    </FormContainer>
  );
};

export default ZoneIntegration;
