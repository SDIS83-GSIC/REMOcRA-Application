import { object } from "yup";
import { useFormikContext } from "formik";
import { URLS } from "../../../routes.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import TYPE_PEI from "../../../enums/TypePeiEnum.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { NatureType } from "./NatureEntity.tsx";

export const prepareNatureValues = (values: NatureType) => ({
  actif: values.natureActif,
  code: values.natureCode,
  libelle: values.natureLibelle,
  typePei: values.natureTypePei,
  protected: values.natureProtected,
});

export const natureValidationSchema = object({
  natureActif: requiredBoolean,
  natureCode: requiredString,
  natureLibelle: requiredString,
  natureTypePei: requiredString,
  natureProtected: requiredBoolean,
});

export const getInitialNatureValue = (data: NatureType) => ({
  natureActif: data?.natureActif ?? null,
  natureCode: data?.natureCode ?? null,
  natureLibelle: data?.natureLibelle ?? null,
  natureTypePei: data?.natureTypePei ?? null,
  natureProtected: data?.natureProtected ?? null,
});

export const NatureForm = () => {
  const listTypePei = Object.values(TYPE_PEI).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });
  const { values, setValues }: any = useFormikContext();

  return (
    <FormContainer>
      <TextInput name="natureCode" label="Code" required={true} />
      <TextInput name="natureLibelle" label="Libellé" required={true} />
      <SelectForm
        name={"natureTypePei"}
        listIdCodeLibelle={listTypePei}
        label="Type de PEI"
        defaultValue={listTypePei?.find((e) => e.code === values.natureTypePei)}
        required={true}
        setValues={setValues}
      />
      <CheckBoxInput name="natureActif" label="Actif" />
      <CheckBoxInput name="natureProtected" label="Protégé" disabled={true} />
      <SubmitFormButtons returnLink={URLS.LIST_NATURE} />
    </FormContainer>
  );
};
