import { useFormikContext } from "formik";
import { object } from "yup";
import { useMemo } from "react";
import { useGet } from "../../../../components/Fetch/useFetch.tsx";
import {
  FormContainer,
  TextInput,
  CheckBoxInput,
} from "../../../../components/Form/Form.tsx";
import SelectForm from "../../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../../components/Form/SubmitFormButtons.tsx";
import url from "../../../../module/fetch.tsx";
import { requiredString } from "../../../../module/validators.tsx";
import SousTypeEvenementType, {
  convertToEnum,
} from "../../../../Entities/SousTypesEvenementsEntity.tsx";
import { referenceTypeGeometrie } from "../../../../enums/Signalement/SousTypeTypeGeometrie.tsx";

export const getInitialValues = (data?: SousTypeEvenementType) => ({
  evenementSousCategorieId: data?.evenementSousCategorieId ?? null,
  evenementSousCategorieCode: data?.evenementSousCategorieCode ?? null,
  evenementSousCategorieLibelle: data?.evenementSousCategorieLibelle ?? null,
  evenementSousCategorieTypeGeometrie: data?.evenementSousCategorieTypeGeometrie
    ? convertToEnum(data.evenementSousCategorieTypeGeometrie)
    : null,
  evenementSousCategorieCriseCategorieId:
    data?.evenementSousCategorieCriseCategorieId ?? null,
  evenementSousCategorieActif: data?.evenementSousCategorieActif ?? null,
});

export const sousTypeValidationSchema = object({
  evenementSousCategorieCode: requiredString,
  evenementSousCategorieLibelle: requiredString,
  evenementSousCategorieCriseCategorieId: requiredString,
});

export const prepareSousTypeValues = (values: SousTypeEvenementType) => ({
  evenementSousCategorieId: values.evenementSousCategorieId,
  evenementSousCategorieCode: values.evenementSousCategorieCode,
  evenementSousCategorieLibelle: values.evenementSousCategorieLibelle,
  evenementSousCategorieTypeGeometrie:
    values.evenementSousCategorieTypeGeometrie,
  evenementSousCategorieCriseCategorieId:
    values.evenementSousCategorieCriseCategorieId,
  evenementSousCategorieActif: values.evenementSousCategorieActif,
});

const SousTypeCreateForm = () => {
  const { setValues, values } = useFormikContext<SousTypeEvenementType>();

  const criseCategories = useGet(url`/api/crise/get-crise-category`);
  const listCriseCategories = useMemo(() => {
    if (!criseCategories.data) {
      return [];
    }
    return criseCategories.data.map(
      (category: {
        criseCategorieId: string;
        criseCategorieCode: string;
        criseCategorieLibelle: string;
      }) => {
        return {
          id: category.criseCategorieId,
          code: category.criseCategorieCode,
          libelle: category.criseCategorieLibelle,
        };
      },
    );
  }, [criseCategories.data]);

  return (
    <FormContainer noValidate>
      <h3 className="mt-1">Informations générales</h3>
      <CheckBoxInput name="evenementSousCategorieActif" label="Actif" />

      <TextInput
        label="Code"
        name="evenementSousCategorieCode"
        required={true}
      />
      <TextInput
        label="Libelle"
        name="evenementSousCategorieLibelle"
        required={true}
      />

      <SelectForm
        name={"evenementSousCategorieId"}
        listIdCodeLibelle={listCriseCategories}
        label="Type de catégorie"
        setValues={setValues}
        required={true}
        defaultValue={
          listCriseCategories?.find(
            (c: any) => c.id === values.evenementSousCategorieCriseCategorieId,
          ) ?? undefined
        }
      />

      <SelectForm
        name="evenementSousCategorieTypeGeometrie"
        label="Type de géométrie"
        listIdCodeLibelle={referenceTypeGeometrie}
        setValues={setValues}
        defaultValue={referenceTypeGeometrie?.find(
          (e) => e.id === values.evenementSousCategorieTypeGeometrie,
        )}
      />

      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default SousTypeCreateForm;
