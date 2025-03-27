import { useFormikContext } from "formik";
import { object } from "yup";
import { useMemo } from "react";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  DateTimeInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import { requiredDate, requiredString } from "../../../module/validators.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { CriseType } from "../../../Entities/CriseEntity.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";

export const getInitialValues = (data?: CriseType) => ({
  typeCrise: data?.typeCriseId ?? null,
  criseLibelle: data?.criseLibelle ?? null,
  criseDescription: data?.criseDescription ?? null,
  listeCommuneId: data?.listeCommune ?? null,
  criseDateDebut: data?.criseDateDebut ?? null,
  listeToponymieId: data?.listeToponymie ?? null,
});

export const criseValidationSchema = object({
  typeCrise: requiredString,
  criseLibelle: requiredString,
  criseDateDebut: requiredDate,
});

export const prepareCriseValues = (values) => ({
  typeCrise: values.typeCrise,
  criseLibelle: values.criseLibelle,
  criseDescription: values.criseDescription,
  listeCommuneId: values.listeCommuneId,
  criseDateDebut: formatDateTimeForDateTimeInput(
    values.criseDateDebut ?? new Date(),
  ),
  listeToponymieId: values.listeToponymieId,
});

const Crise = () => {
  const { setValues, setFieldValue, values } = useFormikContext();

  const typeCriseState = useGet(url`/api/crise/get-type-crise`);
  const communeState = useGet(url`/api/commune/get-libelle-commune`);
  const toponymieList = useGet(url`/api/toponymie/get-libelle-toponymie`);

  // mapper le dictionnaire retourné par "typeCriseState" pour récupérer l'id et le libellé
  const listTypeCrise = useMemo(() => {
    if (!typeCriseState.data) {
      return [];
    }
    return typeCriseState.data.map((crise) => {
      return { id: crise.criseId, libelle: crise.criseNom };
    });
  }, [typeCriseState.data]);

  return (
    <FormContainer noValidate>
      <h3 className="mt-1">Informations générales</h3>
      <SelectForm
        name={"typeCrise"}
        listIdCodeLibelle={listTypeCrise}
        label="Type de la crise"
        required={true}
        setValues={setValues}
        defaultValue={listTypeCrise.find(
          (e: { id: any }) => e.id === values?.typeCriseId,
        )}
      />

      <TextInput label="Nom" name="criseLibelle" required={true} />

      <TextAreaInput
        name="criseDescription"
        label="Description"
        required={false}
      />

      <DateTimeInput
        name="criseDateDebut"
        label="Date et heure d’activation"
        required={true}
      />

      <Multiselect
        name={"listeCommune"}
        label="Liste des communes de la crise"
        options={communeState.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        value={
          values.listeCommuneId?.map((e) =>
            communeState.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(commune) => {
          const communeId = commune.map((e) => e.id);
          communeId.length > 0
            ? setFieldValue("listeCommuneId", communeId)
            : setFieldValue("listeCommuneId", undefined);
        }}
        isClearable={true}
      />

      <Multiselect
        name={"repertoireLieux"}
        label="Répertoire des lieux"
        options={toponymieList.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        isClearable={true}
        required={false}
        value={
          values.listeToponymieId?.map((e) =>
            toponymieList.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(toponymie) => {
          const toponymieId = toponymie.map((e: { id: any }) => e.id);
          toponymieId.length > 0
            ? setFieldValue("listeToponymieId", toponymieId)
            : setFieldValue("listeToponymieId", undefined);
        }}
      />

      <SubmitFormButtons />
    </FormContainer>
  );
};

export default Crise;
