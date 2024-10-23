import { object } from "yup";
import { useFormikContext } from "formik";
import SubmitFormButtons from "../Form/SubmitFormButtons.tsx";
import { CheckBoxInput, FormContainer, TextInput } from "../Form/Form.tsx";
import { requiredBoolean, requiredString } from "../../module/validators.tsx";

type NomenclatureType = {
  code: string;
  libelle: string;
  actif: boolean;
  protected: boolean;
};

export const getInitialValue = (data?: NomenclatureType) => ({
  code: data?.code ?? null,
  libelle: data?.libelle ?? null,
  actif: data?.actif ?? null,
  protected: data?.protected ?? null,
});

export const prepareValues = (values: NomenclatureType) => ({
  code: values.code,
  libelle: values.libelle,
  actif: values.actif,
  protected: values.protected,
});

export const validationSchema = object({
  code: requiredString,
  libelle: requiredString,
  actif: requiredBoolean,
});

export const Nomenclature = ({
  returnLink,
  hasProtectedValue = true,
}: {
  returnLink: string;
  hasProtectedValue?: boolean;
}) => {
  const { values } = useFormikContext<NomenclatureType>();

  return (
    <FormContainer>
      <TextInput
        label="Code"
        name="code"
        required={true}
        disabled={hasProtectedValue && values.protected}
      />
      <TextInput label="Libellé" name="libelle" required={true} />
      <CheckBoxInput name="actif" label="actif" />
      {hasProtectedValue && (
        <CheckBoxInput name="protected" label="Protégé" disabled={true} />
      )}
      <SubmitFormButtons returnLink={returnLink} />
    </FormContainer>
  );
};
