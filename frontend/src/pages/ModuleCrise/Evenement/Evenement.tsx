import { useFormikContext } from "formik";
import { Button } from "react-bootstrap";
import { object } from "yup";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  RangeInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import EvenementType from "../../../Entities/EvenementEntity.tsx";
import { requiredDate, requiredString } from "../../../module/validators.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import FormDocuments, {
  setDocumentInFormData,
} from "../../../components/Form/FormDocuments.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";

export const getInitialValues = (
  data: EvenementType | null,
  geometrie: string | undefined,
  typeEvent?: string | undefined,
) => ({
  evenementLibelle: data?.evenementLibelle ?? null,
  evenementOrigine: data?.evenementOrigine ?? "",
  evenementDescription: data?.evenementDescription ?? "",
  evenementDateDebut: data?.evenementDateDebut ?? null,
  evenementImportance: data?.evenementImportance ?? 1,
  evenementActif: data?.evenementActif ?? false,
  documents: data?.documents ?? [],
  geometrieEvenement: geometrie ?? null,
  evenementTypeId: data?.evenementTypeCriseId ?? typeEvent,
});

export const validationSchema = object({
  evenementLibelle: requiredString,
  evenementDateDebut: requiredDate,
});

export const prepareVariables = (
  values: EvenementType,
  initialData: EvenementType | null,
) => {
  const formData = new FormData();
  setDocumentInFormData(values?.documents, initialData?.documents, formData);

  formData.append("evenementTypeId", values.evenementTypeId);
  formData.append("evenementLibelle", values.evenementLibelle);
  formData.append("evenementOrigine", values.evenementOrigine);
  formData.append("evenementDescription", values.evenementDescription);
  formData.append(
    "evenementDateDebut",
    new Date(values.evenementDateDebut).toISOString(),
  );
  formData.append("evenementImportance", values.evenementImportance);
  formData.append("evenementActif", JSON.stringify(values.evenementActif));
  formData.append(
    "evenementGeometrie",
    JSON.stringify(values.geometrieEvenement),
  );

  return formData;
};

const Evenement = () => {
  const { setValues, setFieldValue, values } =
    useFormikContext<EvenementType>();

  const typeCriseState = useGet(url`/api/crise/evenement/get-type-evenement`);

  const filtre = values.geometrieEvenement
    ? (e) => e.typeEvenementId === values.evenementTypeId
    : (e) => e.typeEvenementGeometrie == null;

  const listeType = typeCriseState?.data?.filter(filtre)?.map((e) => ({
    id: e.typeEvenementId,
    code: e.typeEvenementCode,
    libelle: e.typeEvenementLibelle,
  }));

  return (
    <FormContainer>
      <h3 className="mt-5">Informations générales</h3>

      <SelectForm
        name="evenementTypeId"
        listIdCodeLibelle={listeType}
        label="Type"
        required={true}
        setValues={setValues}
        defaultValue={values.geometrieEvenement ? listeType?.[0] : null}
      />

      <TextInput label="titre" name="evenementLibelle" required={true} />

      <TextAreaInput
        name="evenementDescription"
        label="Description"
        required={false}
      />
      <TextInput label="Origine" name="evenementOrigine" required={false} />

      <DateTimeInput
        name="evenementDateDebut"
        label="Date et heure d’activation"
        required={true}
        value={
          values.evenementDateDebut &&
          formatDateTimeForDateTimeInput(values.evenementDateDebut)
        }
      />

      <RangeInput
        step={1}
        min={0}
        value={+values.evenementImportance}
        name="evenementImportance"
        label="Importance"
        max={5}
      />

      <CheckBoxInput name={"evenementActif"} label={"Clore l'évènement"} />

      {/* // TODO : faire une entrée pour les "tag" */}

      <h3 className="mt-5">Document</h3>
      <FormDocuments
        documents={values.documents}
        setFieldValue={setFieldValue}
        autreFormParam={(index: number) => (
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

      <Button type="submit" variant="primary">
        Valider
      </Button>
    </FormContainer>
  );
};

export default Evenement;
