import { object } from "yup";
import { Button } from "react-bootstrap";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import { URLS } from "../../../routes.tsx";
import { DiametreType } from "./DiametreEntity.tsx";

export const getInitialDiametreValue = (data: DiametreType) => ({
  diametreCode: data?.diametreCode ?? null,
  diametreLibelle: data?.diametreLibelle ?? null,
  diametreActif: data?.diametreActif ?? null,
  diametreProtected: data?.diametreProtected ?? null,
});

export const prepareDiametreValues = (values: DiametreType) => ({
  code: values.diametreCode,
  libelle: values.diametreLibelle,
  actif: values.diametreActif,
  protected: values.diametreProtected,
});

export const diametreValidationSchema = object({
  diametreCode: requiredString,
  diametreLibelle: requiredString,
  diametreActif: requiredBoolean,
});

export const DiametreForm = () => {
  return (
    <FormContainer>
      <TextInput
        placeholder="DIAM100"
        label="Code"
        name="diametreCode"
        required={true}
      />
      <TextInput
        placeholder="Diametre 100mm"
        label="Libellé"
        name="diametreLibelle"
        required={true}
      />
      <CheckBoxInput name="diametreActif" label="Actif" />
      <CheckBoxInput name="diametreProtected" label="Protégé" disabled={true} />
      <Button
        type="button"
        variant="primary"
        href={URLS.DIAMETRE}
        className="mx-1"
      >
        Annuler
      </Button>
      <Button type="submit" variant="primary">
        Valider
      </Button>
    </FormContainer>
  );
};
