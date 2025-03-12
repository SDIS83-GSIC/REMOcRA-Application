import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import url from "../../../module/fetch.tsx";

type SiteType = {
  siteGestionnaireId: string | null;
  siteCode: string;
  siteLibelle: string;
  siteActif: boolean;
};

export const getInitialValues = (data: SiteType) => ({
  siteGestionnaireId: data?.siteGestionnaireId ?? null,
  siteCode: data?.siteCode ?? null,
  siteLibelle: data?.siteLibelle ?? null,
  siteActif: data?.siteActif ?? false,
});

export const validationSchema = object({});

export const prepareVariables = (values: SiteType) => ({
  siteGestionnaireId: values.siteGestionnaireId,
  siteCode: values.siteCode,
  siteLibelle: values.siteLibelle,
  siteActif: values.siteActif,
});

const Site = () => {
  const { setValues } = useFormikContext<SiteType>();
  const { data } = useGet(url`/api/gestionnaire/get`);

  return (
    <FormContainer>
      <h3 className="mt-1">Informations générales</h3>
      <TextInput label="Code" name="siteCode" required={true} />
      <TextInput label="Libellé" name="siteLibelle" required={true} />
      <CheckBoxInput name="siteActif" label="Actif" />
      <SelectForm
        name="siteGestionnaireId"
        label={"Gestionnaire"}
        listIdCodeLibelle={data}
        setValues={setValues}
      />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default Site;
