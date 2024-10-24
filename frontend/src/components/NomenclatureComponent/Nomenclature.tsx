import { useFormikContext } from "formik";
import { object } from "yup";
import { requiredBoolean, requiredString } from "../../module/validators.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";
import { CheckBoxInput, FormContainer, TextInput } from "../Form/Form.tsx";
import SelectForm from "../Form/SelectForm.tsx";
import SubmitFormButtons from "../Form/SubmitFormButtons.tsx";

type NomenclatureType = {
  code: string;
  libelle: string;
  actif: boolean;
  protected: boolean;
  idFk: boolean;
};

export const getInitialValue = (data?: NomenclatureType) => ({
  code: data?.code ?? null,
  libelle: data?.libelle ?? null,
  actif: data?.actif ?? false,
  protected: data?.protected ?? null,
  idFk: data?.idFk ?? null,
});

export const prepareValues = (values: NomenclatureType) => ({
  code: values.code,
  libelle: values.libelle,
  actif: values.actif,
  protected: values.protected,
  idFk: values.idFk,
});

export const validationSchema = object({
  code: requiredString,
  libelle: requiredString,
  actif: requiredBoolean,
});

export const Nomenclature = ({
  returnLink,
  hasProtectedValue = true,
  listeFk,
  libelleFk,
}: {
  returnLink: string;
  hasProtectedValue?: boolean;
  listeFk: IdCodeLibelleType[] | null;
  libelleFk: string | null;
}) => {
  const { values, setValues } = useFormikContext<NomenclatureType>();

  return (
    <FormContainer>
      <TextInput
        label="Code"
        name="code"
        required={true}
        disabled={hasProtectedValue && values.protected}
      />
      <TextInput label="Libellé" name="libelle" required={true} />
      <CheckBoxInput name="actif" label="Actif" />
      {hasProtectedValue && (
        <CheckBoxInput name="protected" label="Protégé" disabled={true} />
      )}
      {listeFk && (
        <SelectForm
          name="idFk"
          label={libelleFk}
          listIdCodeLibelle={listeFk}
          setValues={setValues}
        />
      )}
      <SubmitFormButtons returnLink={returnLink} />
    </FormContainer>
  );
};
