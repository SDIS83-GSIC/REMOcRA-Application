import { useFormikContext } from "formik";
import { object } from "yup";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { TYPE_DROIT_API } from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { OrganismeCompleteType } from "./OrganismeEntity.tsx";

export const prepareOrganismeValues = (values: OrganismeCompleteType) => ({
  actif: values.organismeActif,
  code: values.organismeCode,
  libelle: values.organismeLibelle,
  emailContact: values.organismeEmailContact,
  profilOrganismeId: values.organismeProfilOrganismeId,
  typeOrganismeId: values.organismeTypeOrganismeId,
  zoneIntegrationId: values.organismeZoneIntegrationId,
  parentId: values.organismeParentId,
});

export const organismeValidationSchema = object({
  organismeActif: requiredBoolean,
  organismeCode: requiredString,
  organismeLibelle: requiredString,
  organismeProfilOrganismeId: requiredString,
  organismeTypeOrganismeId: requiredString,
  organismeZoneIntegrationId: requiredString,
});

export const getInitialOrganismeValue = (data: OrganismeType) => ({
  organismeId: data?.organismeId ?? null,
  organismeActif: data?.organismeActif ?? null,
  organismeCode: data?.organismeCode ?? null,
  organismeLibelle: data?.organismeLibelle ?? null,
  organismeEmailContact: data?.organismeEmailContact ?? null,
  organismeProfilOrganismeId: data?.organismeProfilOrganismeId ?? null,
  organismeTypeOrganismeId: data?.organismeTypeOrganismeId ?? null,
  organismeZoneIntegrationId: data?.organismeZoneIntegrationId ?? null,
  organismeParentId: data?.organismeParentId ?? null,
});

export type ProfilOrganismeType = {
  profilOrganismeId: string;
  profilOrganismeActif: boolean;
  profilOrganismeCode: string;
  profilOrganismeLibelle: string;
  profilOrganismeTypeOrganismeId: string;
};

export type TypeOrganismeType = {
  typeOrganismeId: string;
  typeOrganismeActif: boolean;
  typeOrganismeProtected: boolean;
  typeOrganismeCode: string;
  typeOrganismeLibelle: string;
  typeOrganismeDroitApi: TYPE_DROIT_API[];
};

export type ZoneIntegrationType = {
  zoneIntegrationId: string;
  zoneIntegrationActif: boolean;
  zoneIntegrationCode: string;
  zoneIntegrationLibelle: string;
  zoneIntegrationGeometrie: string;
  zoneIntegrationType: string;
};

export type OrganismeType = {
  organismeId: string;
  organismeActif: boolean;
  organismeCode: string;
  organismeLibelle: string;
  organismeEmailContact: string;
  organismeProfilOrganismeId: string;
  organismeTypeOrganismeId: string;
  organismeZoneIntegrationId: string;
  organismeParentId: string;
};

export const OrganismeForm = () => {
  const { values, setValues }: any = useFormikContext();
  const profilOrganisme = useGet(url`/api/profil-organisme/get-active`);
  const typeOrganisme = useGet(url`/api/type-organisme/get-active`);
  const zoneIntegration = useGet(url`/api/zone-integration/get-active`);
  const organisme = useGet(url`/api/organisme/get-active`);
  if (
    !profilOrganisme.isResolved ||
    !typeOrganisme.isResolved ||
    !zoneIntegration.isResolved ||
    !organisme.isResolved
  ) {
    return;
  }

  const profilOrganismeList: IdCodeLibelleType[] = profilOrganisme.data.map(
    (e: ProfilOrganismeType) => {
      return {
        id: e.profilOrganismeId,
        libelle: e.profilOrganismeLibelle,
        code: e.profilOrganismeCode,
      };
    },
  );
  const typeOrganismeList: IdCodeLibelleType[] = typeOrganisme.data.map(
    (e: TypeOrganismeType) => {
      return {
        id: e.typeOrganismeId,
        libelle: e.typeOrganismeLibelle,
        code: e.typeOrganismeCode,
      };
    },
  );

  const organismeList: IdCodeLibelleType[] = organisme.data.map(
    (e: OrganismeType) => {
      return {
        id: e.organismeId,
        libelle: e.organismeLibelle,
        code: e.organismeCode,
      };
    },
  );

  const defaultProfilOrganisme = profilOrganismeList.find((e) => {
    return e.id === values.organismeProfilOrganismeId;
  });
  const defaultTypeOrganisme = typeOrganismeList.find((e) => {
    return e.id === values.organismeTypeOrganismeId;
  });
  const defaultZoneIntegration = zoneIntegration.data.find((e) => {
    return e.id === values.organismeZoneIntegrationId;
  });
  const defaultOrganismeParent = organismeList.find((e) => {
    return e.id === values.organismeParentId;
  });

  return (
    <FormContainer>
      <TextInput name="organismeCode" label="Code" required={true} />
      <TextInput name="organismeLibelle" label="Libellé" required={true} />
      <TextInput name="organismeEmailContact" label="Email" required={false} />
      <SelectForm
        name="organismeProfilOrganismeId"
        label="Profil"
        listIdCodeLibelle={profilOrganismeList}
        setValues={setValues}
        required={true}
        defaultValue={defaultProfilOrganisme}
      />
      <SelectForm
        name="organismeTypeOrganismeId"
        label="Type"
        listIdCodeLibelle={typeOrganismeList}
        setValues={setValues}
        required={true}
        defaultValue={defaultTypeOrganisme}
      />
      <SelectForm
        name="organismeZoneIntegrationId"
        label="Zone de compétence"
        listIdCodeLibelle={zoneIntegration.data}
        setValues={setValues}
        required={true}
        defaultValue={defaultZoneIntegration}
      />
      <SelectForm
        name="organismeParentId"
        label="Organisme parent"
        listIdCodeLibelle={organismeList}
        setValues={setValues}
        required={false}
        defaultValue={defaultOrganismeParent}
      />
      <CheckBoxInput name="organismeActif" label="Actif" />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};
