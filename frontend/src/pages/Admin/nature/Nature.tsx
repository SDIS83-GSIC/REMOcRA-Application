import { useFormikContext } from "formik";
import { array, object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import TYPE_PEI from "../../../enums/TypePeiEnum.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import { NatureType } from "./NatureEntity.tsx";

export const prepareNatureValues = (values: NatureType) => ({
  actif: values.natureActif,
  code: values.natureCode,
  libelle: values.natureLibelle,
  typePei: values.natureTypePei,
  protected: values.natureProtected,
  diametreIds: values.diametreIds,
});

export const natureValidationSchema = object({
  natureActif: requiredBoolean,
  natureCode: requiredString,
  natureLibelle: requiredString,
  natureTypePei: requiredString,
  natureProtected: requiredBoolean,
  diametreIds: array(),
});

export const getInitialNatureValue = (data: NatureType) => ({
  natureActif: data?.natureActif ?? null,
  natureCode: data?.natureCode ?? null,
  natureLibelle: data?.natureLibelle ?? null,
  natureTypePei: data?.natureTypePei ?? null,
  natureProtected: data?.natureProtected ?? null,
  diametreIds: data?.diametreIds ?? [],
});

export const NatureForm = () => {
  const listTypePei = Object.values(TYPE_PEI).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });
  const { values, setValues, setFieldValue }: any = useFormikContext();

  const diametreState = useGet(url`/api/nomenclatures/diametre`);

  return (
    <FormContainer>
      <TextInput
        name="natureCode"
        label="Code"
        required={true}
        disabled={values.natureProtected}
      />
      <TextInput name="natureLibelle" label="Libellé" required={true} />
      <SelectForm
        name={"natureTypePei"}
        listIdCodeLibelle={listTypePei}
        label="Type de PEI"
        defaultValue={listTypePei?.find((e) => e.code === values.natureTypePei)}
        required={true}
        disabled={values.natureProtected}
        setValues={setValues}
      />
      <CheckBoxInput name="natureActif" label="Actif" />
      <CheckBoxInput name="natureProtected" label="Protégé" disabled={true} />

      <Multiselect
        name={"diametreIds"}
        label="Diamètres associés à cette nature"
        required={false}
        options={diametreState?.data ? Object.values(diametreState?.data) : []}
        getOptionValue={(t) => t.diametreId}
        getOptionLabel={(t) => t.diametreLibelle}
        value={
          values?.diametreIds?.map((e) =>
            Object.values(diametreState?.data)?.find((r) => r.diametreId === e),
          ) ?? undefined
        }
        onChange={(diametre) => {
          const diametreId = diametre.map((e) => e.diametreId);
          diametreId.length > 0
            ? setFieldValue("diametreIds", diametreId)
            : setFieldValue("diametreIds", []);
        }}
      />

      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};
