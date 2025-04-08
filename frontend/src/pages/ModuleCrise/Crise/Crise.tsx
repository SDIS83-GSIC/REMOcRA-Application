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
import { requiredArray, requiredDate, requiredString } from "../../../module/validators.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { CriseType } from "../../../Entities/CriseEntity.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";

export const getInitialValues = (data?: CriseType) => ({
  typeCriseId: data?.typeCriseId ?? null,
  criseLibelle: data?.criseLibelle ?? null,
  criseDescription: data?.criseDescription ?? null,
  listeCommuneId: data?.listeCommuneId ?? null,
  criseDateDebut: data?.criseDateDebut
    ? formatDateTimeForDateTimeInput(data?.criseDateDebut)
    : formatDateTimeForDateTimeInput(new Date()),
  listeToponymieId: data?.listeToponymieId ?? null,
});

export const criseValidationSchema = object({
  typeCriseId: requiredString,
  criseLibelle: requiredString,
  criseDateDebut: requiredDate,
  listeCommuneId: requiredArray,
});

export const prepareCriseValues = (values: CriseType) => ({
  typeCriseId: values.typeCriseId,
  criseLibelle: values.criseLibelle,
  criseDescription: values.criseDescription,
  listeCommuneId: values.listeCommuneId,
  criseDateDebut: new Date(values.criseDateDebut).toISOString(),
  listeToponymieId: values.listeToponymieId,
});

const Crise = () => {
  const { setValues, setFieldValue, values } = useFormikContext<CriseType>();

  const typeCriseState = useGet(url`/api/crise/get-type-crise`);
  const communeState = useGet(url`/api/commune/get-libelle-commune`);
  const toponymieList = useGet(url`/api/toponymie/get-libelle-toponymie`);

  // mapper le dictionnaire retourné par "typeCriseState" pour récupérer l'id et le libellé
  const listTypeCrise = useMemo(() => {
    if (!typeCriseState.data) {
      return [];
    }
    return typeCriseState.data.map((crise) => {
      return { id: crise.criseId, code: crise.criseNom, libelle: crise.criseNom };
    });
  }, [typeCriseState.data]);

  return (
    <FormContainer noValidate>
      <h3 className="mt-1">Informations générales</h3>
      <SelectForm
        name={"typeCriseId"}
        listIdCodeLibelle={listTypeCrise}
        label="Type de la crise"
        required={true}
        setValues={setValues}
        defaultValue={listTypeCrise.find(
          (e: IdCodeLibelleType) => e.id === values?.typeCriseId,
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
        value={values.criseDateDebut}
        required={true}
      />

      <Multiselect
        name={"listeCommuneId"}
        label="Liste des communes de la crise"
        options={communeState.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        value={
          values.listeCommuneId?.map((e: any) =>
            communeState.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(commune) => {
          const communeId = commune.map((e: any) => e.id);
          communeId.length > 0
            ? setFieldValue("listeCommuneId", communeId)
            : setFieldValue("listeCommuneId", undefined);
        }}
        isClearable={true}
      />

      <Multiselect
        name={"listeTyponymieId"}
        label="Répertoire des lieux"
        options={toponymieList.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        isClearable={true}
        required={false}
        value={
          values.listeToponymieId?.map((e: any) =>
            toponymieList.data?.find((c: IdCodeLibelleType) => c.id === e),
          ) ?? undefined
        }
        onChange={(toponymie) => {
          const toponymieId = toponymie.map((e: any) => e.id);
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
