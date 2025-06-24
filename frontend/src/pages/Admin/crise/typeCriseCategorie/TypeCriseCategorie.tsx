import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import {
  FormContainer,
  SelectInput,
  TextInput,
} from "../../../../components/Form/Form.tsx";
import SelectForm from "../../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";
import TYPE_GEOMETRIE from "../../../../enums/TypeGeometrie.tsx";
import url from "../../../../module/fetch.tsx";
import { requiredString } from "../../../../module/validators.tsx";

type TypeCriseCategorieType = {
  typeCriseCategorieId: string;
  typeCriseCategorieCode: string;
  typeCriseCategorieLibelle: string;
  typeCriseCategorieTypeGeometrie: TYPE_GEOMETRIE;
  typeCriseCategorieCriseCategorieId: string;
};

export const prepareValues = (values: TypeCriseCategorieType) => ({
  typeCriseCategorieId: values.typeCriseCategorieId,
  typeCriseCategorieCode: values.typeCriseCategorieCode,
  typeCriseCategorieLibelle: values.typeCriseCategorieLibelle,
  typeCriseCategorieTypeGeometrie: values.typeCriseCategorieTypeGeometrie,
  typeCriseCategorieCriseCategorieId: values.typeCriseCategorieCriseCategorieId,
});

export const typeCriseCategorieValidationSchema = object({
  typeCriseCategorieCode: requiredString,
  typeCriseCategorieLibelle: requiredString,
  typeCriseCategorieTypeGeometrie: requiredString,
  typeCriseCategorieCriseCategorieId: requiredString,
});

export const getInitialTypeCriseCategorieValue = (
  data: TypeCriseCategorieType,
) => ({
  typeCriseCategorieId: data.typeCriseCategorieId ?? null,
  typeCriseCategorieCode: data.typeCriseCategorieCode ?? null,
  typeCriseCategorieLibelle: data.typeCriseCategorieLibelle ?? null,
  typeCriseCategorieTypeGeometrie: data.typeCriseCategorieTypeGeometrie ?? null,
  typeCriseCategorieCriseCategorieId:
    data.typeCriseCategorieCriseCategorieId ?? null,
});

export const TypeCriseCategorie = () => {
  const listTypeGeometrie = Object.values(TYPE_GEOMETRIE).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });
  const { values, setValues, setFieldValue }: any = useFormikContext();

  const criseCategorieState = useGet(url`/api/nomenclatures/crise_categorie`);

  return (
    criseCategorieState?.data && (
      <FormContainer>
        <TextInput name="typeCriseCategorieCode" label="Code" required={true} />
        <TextInput
          name="typeCriseCategorieLibelle"
          label="Libellé"
          required={true}
        />
        <SelectForm
          name={"typeCriseCategorieTypeGeometrie"}
          listIdCodeLibelle={listTypeGeometrie}
          label="Type de géométrie"
          defaultValue={listTypeGeometrie?.find(
            (e) => e.code === values.typeCriseCategorieTypeGeometrie,
          )}
          required={true}
          setValues={setValues}
        />

        <SelectInput
          name={"typeCriseCategorieCriseCategorie"}
          label="Catégorie de la crise"
          required={false}
          options={
            criseCategorieState?.data
              ? Object.values(criseCategorieState?.data)
              : []
          }
          getOptionValue={(t) => t.criseCategorieId}
          getOptionLabel={(t) => t.criseCategorieLibelle}
          defaultValue={
            Object.values(criseCategorieState.data)?.find(
              (r) =>
                r.criseCategorieId ===
                values.typeCriseCategorieCriseCategorieId,
            ) ?? null
          }
          onChange={(e) => {
            setFieldValue(
              `typeCriseCategorieCriseCategorieId`,
              Object.values(criseCategorieState?.data)?.find(
                (categorie) =>
                  categorie.criseCategorieId === e.criseCategorieId,
              )?.criseCategorieId,
            );
          }}
        />

        <SubmitFormButtons returnLink={true} />
      </FormContainer>
    )
  );
};
