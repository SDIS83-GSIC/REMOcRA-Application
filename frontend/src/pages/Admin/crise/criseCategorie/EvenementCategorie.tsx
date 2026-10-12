import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  TextInput,
} from "../../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";
import {
  EvenementCategorieType,
  TypeCriseType,
} from "../../../../Entities/EvenementCategorieEntity.tsx";
import url from "../../../../module/fetch.tsx";
import { requiredString } from "../../../../module/validators.tsx";

export const prepareValues = (values: EvenementCategorieType) => ({
  evenementCategorieId: values.evenementCategorieId,
  evenementCategorieCode: values.evenementCategorieCode,
  evenementCategorieLibelle: values.evenementCategorieLibelle,
  evenementCategorieActif: values.evenementCategorieActif,
  evenementCategorieTypeCrise: values.evenementCategorieTypeCrise,
});

export const evenementCategorieValidationSchema = object({
  evenementCategorieCode: requiredString,
  evenementCategorieLibelle: requiredString,
});

export const getInitialEvenementCategorieValue = (
  data?: EvenementCategorieType,
) => {
  return {
    evenementCategorieId: data?.evenementCategorieId ?? null,
    evenementCategorieCode: data?.evenementCategorieCode ?? null,
    evenementCategorieLibelle: data?.evenementCategorieLibelle ?? null,
    evenementCategorieActif: data?.evenementCategorieActif ?? true,
    evenementCategorieTypeCrise: data?.evenementCategorieTypeCrise ?? [],
  };
};

export const EvenementCategorie = () => {
  const { values, setFieldValue } = useFormikContext<EvenementCategorieType>();
  const typeCriseState = useGet(url`/api/nomenclatures/type_crise`);
  const listeTypeCrise: TypeCriseType[] = typeCriseState?.data
    ? Object.values(typeCriseState.data)
    : [];

  return (
    typeCriseState?.data && (
      <FormContainer>
        <CheckBoxInput name={"evenementCategorieActif"} label={"Actif"} />

        <TextInput name="evenementCategorieCode" label="Code" />
        <TextInput name="evenementCategorieLibelle" label="Libellé" />

        <Multiselect
          name={"evenementCategorieTypeCrise"}
          label="Types de crise associés"
          required={false}
          options={listeTypeCrise}
          getOptionValue={(t: TypeCriseType) => t.typeCriseId}
          getOptionLabel={(t: TypeCriseType) => t.typeCriseLibelle}
          value={values.evenementCategorieTypeCrise?.map((e) =>
            listeTypeCrise.find((r) => r.typeCriseId === e.typeCriseId),
          )}
          onChange={(typeCrise: TypeCriseType[]) => {
            setFieldValue("evenementCategorieTypeCrise", typeCrise ?? []);
          }}
        />

        <SubmitFormButtons returnLink={true} />
      </FormContainer>
    )
  );
};
