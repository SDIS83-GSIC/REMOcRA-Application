import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  FileInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

type BlocDocumentType = {
  blocDocumentLibelle: string;
  blocDocumentDecription: string;
  listeThematiqueId: string[];
  listeProfilDroitId: string[];
  document: any;
};

export const getInitialValues = (data?: BlocDocumentType) => ({
  blocDocumentLibelle: data?.blocDocumentLibelle ?? null,
  blocDocumentDecription: data?.blocDocumentDecription ?? null,
  listeThematiqueId: data?.listeThematiqueId ?? null,
  listeProfilDroitId: data?.listeProfilDroitId ?? null,
  document: null,
});

export const validationSchema = object({});

export const prepareVariables = (values: BlocDocumentType) => {
  const formData = new FormData();

  formData.append("document", values.document);
  formData.append("blocDocumentLibelle", values.blocDocumentLibelle);
  formData.append("blocDocumentDecription", values.blocDocumentDecription);
  formData.append(
    "listeThematiqueId",
    JSON.stringify(values.listeThematiqueId),
  );
  formData.append(
    "listeProfilDroitId",
    JSON.stringify(values.listeProfilDroitId),
  );

  return formData;
};

const BlocDocument = () => {
  const { values, setFieldValue } = useFormikContext<BlocDocumentType>();
  const thematiqueState = useGet(url`/api/thematique/actif`);
  const profilDroitState = useGet(url`/api/profil-droit`);

  return (
    <FormContainer>
      <TextInput label="Libellé" name="blocDocumentLibelle" required={false} />
      <TextAreaInput
        label="Description"
        name="blocDocumentDescription"
        required={false}
      />
      <FileInput
        name="document"
        accept="*.*"
        label="Document"
        required={true}
        onChange={(e) => setFieldValue("document", e.target.files[0])}
      />

      <Multiselect
        name={"listeThematiqueId"}
        label="Thématiques concernées par le document"
        options={thematiqueState?.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        value={
          values?.listeThematiqueId?.map((e) =>
            thematiqueState?.data?.find((r: IdCodeLibelleType) => r.id === e),
          ) ?? undefined
        }
        onChange={(thematique) => {
          const thematiqueId = thematique.map((e) => e.id);
          thematiqueId.length > 0
            ? setFieldValue("listeThematiqueId", thematiqueId)
            : setFieldValue("listeThematiqueId", undefined);
        }}
        isClearable={true}
        required={false}
      />

      <Multiselect
        name={"listeProfilDroitId"}
        label="Profils droit concernés par le document"
        options={profilDroitState?.data}
        getOptionValue={(t) => t.id}
        getOptionLabel={(t) => t.libelle}
        value={
          values?.listeProfilDroitId?.map((e) =>
            profilDroitState?.data?.find((r: IdCodeLibelleType) => r.id === e),
          ) ?? undefined
        }
        onChange={(profilDroit) => {
          const profilDroitId = profilDroit.map((e) => e.id);
          profilDroitId.length > 0
            ? setFieldValue("listeProfilDroitId", profilDroitId)
            : setFieldValue("listeProfilDroitId", undefined);
        }}
        isClearable={true}
        required={false}
      />

      <SubmitFormButtons returnLink={URLS.LIST_BLOC_DOCUMENT} />
    </FormContainer>
  );
};

export default BlocDocument;
