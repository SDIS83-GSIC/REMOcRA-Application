import { useFormikContext } from "formik";
import { object } from "yup";
import { useMemo } from "react";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  RangeInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import FormDocuments, {
  setDocumentInFormData,
} from "../../../components/Form/FormDocuments.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import TagInput from "../../../components/InputTag/InputTag.tsx";
import EvenementType from "../../../Entities/EvenementEntity.tsx";
import { requiredDate, requiredString } from "../../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import { GenererComplementForm } from "../../../utils/buildDynamicForm.tsx";

export const getInitialValues = (
  data: EvenementType | null,
  geometrie: string | undefined,
  sousCategorieEvenement?: string | undefined,
  parameters?: any[] | null,
) => {
  const sousCategorieEvenementId =
    data?.evenementSousCategorieId ?? sousCategorieEvenement;
  return {
    evenementLibelle: data?.evenementLibelle ?? null,
    evenementOrigine: data?.evenementOrigine ?? "",
    evenementDescription: data?.evenementDescription ?? "",
    evenementDateConstat: data?.evenementDateConstat ?? new Date(),
    evenementImportance: data?.evenementImportance ?? 1,
    evenementEstFerme: data?.evenementEstFerme ?? false,
    documents: data?.documents ?? [],
    evenementTags: data?.evenementTags ?? [],
    geometrieEvenement: geometrie ?? null,
    evenementSousCategorieId: sousCategorieEvenementId,
    evenementSousCategorieComplement:
      data?.evenementParametre ??
      parameters
        ?.find(
          (param) =>
            param.evenementSousCategorieId === sousCategorieEvenementId,
        )
        ?.evenementSousCategorieComplement.map(
          (param: {
            dynamicFormParametreId: any;
            dynamicFormParametreValeurDefaut: any;
          }) => ({
            idParam: param.dynamicFormParametreId,
            valueParam: param.dynamicFormParametreValeurDefaut ?? "",
          }),
        ),
  };
};

export const validationSchema = object({
  evenementLibelle: requiredString,
  evenementDateConstat: requiredDate,
});

export const prepareVariables = (
  values: EvenementType,
  initialData: EvenementType | null,
  userId: string,
) => {
  const formData = new FormData();
  setDocumentInFormData(values?.documents, initialData?.documents, formData);

  formData.append("evenementSousCategorieId", values.evenementSousCategorieId);
  formData.append("evenementLibelle", values.evenementLibelle);
  formData.append("evenementOrigine", values.evenementOrigine);
  formData.append("evenementDescription", values.evenementDescription);
  formData.append(
    "evenementDateConstat",
    new Date(values.evenementDateConstat).toISOString(),
  );
  formData.append("evenementImportance", values.evenementImportance);
  formData.append(
    "evenementEstFerme",
    JSON.stringify(values.evenementEstFerme),
  );
  formData.append(
    "evenementGeometrie",
    JSON.stringify(values.geometrieEvenement),
  );
  formData.append("evenementUtilisateurId", userId);
  formData.append("evenementTags", values.evenementTags.join());
  formData.append(
    "evenementParametre",
    JSON.stringify(values.evenementSousCategorieComplement),
  );
  return formData;
};

const Evenement = ({
  isReadOnly,
  sousCategoriesEvenement,
}: {
  isReadOnly: any;
  sousCategoriesEvenement: any;
}) => {
  const { setValues, setFieldValue, values } =
    useFormikContext<EvenementType>();

  const filtre = values.geometrieEvenement
    ? (e: { evenementSousCategorieId: string }) =>
        e.evenementSousCategorieId === values.evenementSousCategorieId
    : (e: { typeEvenementGeometrie: null }) => e.typeEvenementGeometrie == null;

  const listeSousCategories = sousCategoriesEvenement
    ?.filter(filtre)
    ?.map((e: any) => ({
      id: e.evenementSousCategorieId,
      code: e.evenementSousCategorieCode,
      libelle: e.evenementSousCategorieLibelle,
      parameters: e.evenementSousCategorieComplement,
    }));

  const sousCategorie = useMemo(() => {
    return values.evenementSousCategorieId
      ? (listeSousCategories?.find(
          (e: { id: string }) => e.id === values.evenementSousCategorieId,
        ) ?? null)
      : null;
  }, [values.evenementSousCategorieId, listeSousCategories]);

  const paramChange = (name: string, value: string) => {
    setFieldValue(name, value);
  };

  return (
    <FormContainer>
      <h3 className="mt-5">Informations générales</h3>

      <SelectForm
        disabled={values.geometrieEvenement ? true : isReadOnly}
        name="evenementSousCategorieId"
        listIdCodeLibelle={listeSousCategories}
        label="Type"
        required={true}
        setValues={setValues}
        defaultValue={sousCategorie}
      />

      <TextInput
        disabled={isReadOnly}
        label="titre"
        name="evenementLibelle"
        required={true}
      />

      <TextAreaInput
        disabled={isReadOnly}
        name="evenementDescription"
        label="Description"
        required={false}
      />
      <TextInput
        disabled={isReadOnly}
        label="Origine"
        name="evenementOrigine"
        required={false}
      />

      <DateTimeInput
        readOnly={isReadOnly}
        name="evenementDateConstat"
        label="Date et heure d’activation"
        required={true}
        value={
          values.evenementDateConstat &&
          formatDateTimeForDateTimeInput(values.evenementDateConstat)
        }
      />

      <RangeInput
        disabled={isReadOnly}
        step={1}
        min={0}
        value={+values.evenementImportance}
        name="evenementImportance"
        label="Importance"
        max={5}
      />

      <CheckBoxInput
        disabled={isReadOnly}
        name={"evenementEstFerme"}
        label={"Clore l'évènement"}
      />

      <TagInput
        onTagsChange={(tags) => setFieldValue("evenementTags", tags)}
        name={"evenementTag"}
        label={"Liste des tags"}
        disabled={isReadOnly}
        defaultTags={values.evenementTags}
      />

      <h3 className="mt-5">Document</h3>
      <FormDocuments
        disabled={isReadOnly}
        documents={values.documents}
        setFieldValue={setFieldValue}
        otherFormParam={(index: number) => (
          <>
            <TextInput
              label="Nom"
              name={`documents[${index}].evenementDocumentLibelle`}
              required={false}
            />
          </>
        )}
        defaultOtherProperties={{
          levenementDocumentLibelle: null,
        }}
      />

      {sousCategorie?.parameters?.length > 0 && (
        <>
          <h3 className="mt-5">Complément</h3>
          <GenererComplementForm
            listeWithParametre={sousCategorie.parameters}
            setFieldValue={setFieldValue}
            isReadOnly={isReadOnly}
            onParamChange={paramChange}
            values={values}
          />
        </>
      )}

      <SubmitFormButtons disabledValide={isReadOnly} />
    </FormContainer>
  );
};

export default Evenement;
