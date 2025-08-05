import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  FormContainer,
  Multiselect,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import FormDocuments, {
  setDocumentInFormData,
} from "../../../components/Form/FormDocuments.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { EtudeType } from "../../../Entities/EtudeEntity.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";

export const getInitialValues = (data?: EtudeType) => ({
  typeEtudeId: data?.typeEtudeId ?? null,
  etudeNumero: data?.etudeNumero ?? null,
  etudeLibelle: data?.etudeLibelle ?? null,
  etudeDescription: data?.etudeDescription ?? null,
  listeCommuneId: data?.listeCommuneId ?? null,
  documents: data?.documents ?? [],
});

export const validationSchema = object({});
export const prepareVariables = (
  values: EtudeType,
  initialData?: EtudeType,
) => {
  const formData = new FormData();
  setDocumentInFormData(
    values?.documents,
    initialData?.documents || [],
    formData,
  );

  formData.append("typeEtudeId", values.typeEtudeId);
  formData.append("etudeNumero", values.etudeNumero);
  formData.append("etudeLibelle", values.etudeLibelle);
  values.etudeDescription &&
    formData.append("etudeDescription", values.etudeDescription);
  formData.append("listeCommuneId", JSON.stringify(values.listeCommuneId));

  return formData;
};

const Etude = () => {
  const { setValues, setFieldValue, values } = useFormikContext<EtudeType>();
  const typeEtudeState = useGet(url`/api/couverture-hydraulique/type-etudes`);
  const communeState = useGet(url`/api/commune/get-libelle-commune`);

  return (
    <>
      <FormContainer>
        <h3 className="mt-1">Informations générales</h3>
        <SelectForm
          name={"typeEtudeId"}
          listIdCodeLibelle={typeEtudeState?.data}
          label="Type de l'étude"
          defaultValue={typeEtudeState?.data?.find(
            (e: IdCodeLibelleType) => e.id === values.typeEtudeId,
          )}
          required={true}
          setValues={setValues}
        />
        <TextInput label="Numéro" name="etudeNumero" required={true} />
        <TextInput label="Nom" name="etudeLibelle" required={true} />
        <TextAreaInput
          name="etudeDescription"
          label="Description"
          required={false}
        />
        <h3 className="mt-5">Communes de l&apos;étude</h3>
        <Multiselect
          name={"listeCommune"}
          label="Liste des communes de l'étude"
          options={communeState?.data}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          value={
            values?.listeCommuneId?.map((e) =>
              communeState?.data?.find((c: IdCodeLibelleType) => c.id === e),
            ) ?? undefined
          }
          onChange={(commune) => {
            const communeId = commune.map((e: IdCodeLibelleType) => e.id);
            communeId.length > 0
              ? setFieldValue("listeCommuneId", communeId)
              : setFieldValue("listeCommuneId", undefined);
          }}
          isClearable={true}
        />
        <h3 className="mt-5">Documents liés à l&apos;étude</h3>
        <FormDocuments
          documents={values.documents}
          setFieldValue={setFieldValue}
          otherFormParam={(index: number) => (
            <>
              <TextInput
                label="Nom"
                name={`documents[${index}].etudeDocumentLibelle`}
                required={false}
              />
            </>
          )}
          defaultOtherProperties={{
            letudeDocumentLibelle: null,
          }}
        />
        <SubmitFormButtons />
      </FormContainer>
    </>
  );
};

export default Etude;
