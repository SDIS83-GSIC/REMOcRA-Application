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

type TypeEvenementSousCategorieType = {
  typeEvenementCategorieId: string;
  typeEvenementCategorieCode: string;
  typeEvenementCategorieLibelle: string;
  typeEvenementCategorieGeometrie: TYPE_GEOMETRIE;
  typeEvenementCategorieEvenementCategorieId: string;
};

export const prepareValues = (values: TypeEvenementSousCategorieType) => ({
  evenementSousCategorieId: values.typeEvenementCategorieId,
  evenementSousCategorieCode: values.typeEvenementCategorieCode,
  evenementSousCategorieLibelle: values.typeEvenementCategorieLibelle,
  evenementSousCategorieGeometrie: values.typeEvenementCategorieGeometrie,
  typeEvenementSousCategorieId:
    values.typeEvenementCategorieEvenementCategorieId,
});

export const typeEvenementCategorieValidationSchema = object({
  evenementSousCategorieCode: requiredString,
  evenementSousCategorieLibelle: requiredString,
  evenementSousCategorieTypeGeometrie: requiredString,
  evenementSousCategorieEvenementId: requiredString,
});

export const getInitialEvenementSousCategorieValue = (
  data: TypeEvenementSousCategorieType,
) => ({
  evenementSousCategorieId: data.typeEvenementCategorieId ?? null,
  evenementSousCategorieCode: data.typeEvenementCategorieCode ?? null,
  evenementSousCategorieLibelle: data.typeEvenementCategorieLibelle ?? null,
  evenementSousCategorieTypeGeometrie:
    data.typeEvenementCategorieGeometrie ?? null,
  evenementSousCategorieEvenementCategorieId:
    data.typeEvenementCategorieEvenementCategorieId ?? null,
});

export const EvenementSousCategorie = () => {
  const listTypeGeometrie = Object.values(TYPE_GEOMETRIE).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });
  const { values, setValues, setFieldValue }: any = useFormikContext();

  const evenementCategorieState = useGet(
    url`/api/nomenclatures/evenement_categorie`,
  );

  return (
    evenementCategorieState?.data && (
      <FormContainer>
        <TextInput
          name="typeEvenementCategorieCode"
          label="Code"
          required={true}
        />
        <TextInput
          name="typeEvenementCategorieLibelle"
          label="Libellé"
          required={true}
        />
        <SelectForm
          name={"evenementSousCategorieTypeGeometrie"}
          listIdCodeLibelle={listTypeGeometrie}
          label="Type de géométrie"
          defaultValue={listTypeGeometrie?.find(
            (e) => e.code === values.evenementSousCategorieTypeGeometrie,
          )}
          required={true}
          setValues={setValues}
        />

        <SelectInput
          name={"evenementSousCategorieEvenementCategorie"}
          label="Catégorie de l'évènement"
          required={false}
          options={
            evenementCategorieState?.data
              ? Object.values(evenementCategorieState?.data)
              : []
          }
          getOptionValue={(t) => t.evenementCategorieId}
          getOptionLabel={(t) => t.evenementCategorieLibelle}
          defaultValue={
            Object.values(evenementCategorieState.data)?.find(
              (r) =>
                r.evenementCategorieId ===
                values.evenementSousCategorieEvenementCategorieId,
            ) ?? null
          }
          onChange={(e) => {
            setFieldValue(
              `evenementSousCategorieEvenementCategorieId`,
              Object.values(evenementCategorieState?.data)?.find(
                (categorie: any) =>
                  categorie.evenementCategorieId === e.evenementCategorieId,
              )?.evenementCategorieId,
            );
          }}
        />

        <SubmitFormButtons returnLink={true} />
      </FormContainer>
    )
  );
};
