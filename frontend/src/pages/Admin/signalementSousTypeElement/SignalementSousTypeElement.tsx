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
import { referenceTypeGeometrie } from "../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

const SignalementSousTypeElement = () => {
  const { values, setValues } =
    useFormikContext<SignalementSousTypeElementType>();
  const listeTypeElement: IdCodeLibelleType[] = useGet(
    url`/api/signalement-sous-type-element/ref`,
  ).data;

  return (
    <FormContainer>
      <TextInput
        label="Code"
        name="signalementSousTypeElementCode"
        required={true}
      />
      <TextInput
        label="Libellé"
        name="signalementSousTypeElementLibelle"
        required={true}
      />
      <CheckBoxInput name="signalementSousTypeElementActif" label="Actif" />
      <SelectForm
        name="signalementSousTypeElementTypeElement"
        label="Type élément parent"
        listIdCodeLibelle={listeTypeElement}
        setValues={setValues}
        defaultValue={listeTypeElement?.find(
          (e) => e.id === values.signalementSousTypeElementTypeElement,
        )}
        required={false} // ISO V2
      />
      <SelectForm
        name="signalementSousTypeElementTypeGeometrie"
        label="Type de géométrie"
        listIdCodeLibelle={referenceTypeGeometrie}
        setValues={setValues}
        defaultValue={referenceTypeGeometrie?.find(
          (e) => e.id === values.signalementSousTypeElementTypeGeometrie,
        )}
        required={true} // ISO V2
      />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default SignalementSousTypeElement;

type SignalementSousTypeElementType = {
  signalementSousTypeElementCode: string;
  signalementSousTypeElementLibelle: string;
  signalementSousTypeElementActif: boolean;
  signalementSousTypeElementTypeElement?: string;
  signalementSousTypeElementTypeGeometrie: string;
};

export const getInitialValue = (data?: SignalementSousTypeElementType) => ({
  signalementSousTypeElementCode: data?.signalementSousTypeElementCode ?? null,
  signalementSousTypeElementLibelle:
    data?.signalementSousTypeElementLibelle ?? null,
  signalementSousTypeElementActif:
    data?.signalementSousTypeElementActif ?? false,
  signalementSousTypeElementTypeElement:
    data?.signalementSousTypeElementTypeElement ?? null,
  signalementSousTypeElementTypeGeometrie:
    data?.signalementSousTypeElementTypeGeometrie ?? null,
});

export const prepareValues = (values: SignalementSousTypeElementType) => ({
  signalementSousTypeElementCode: values.signalementSousTypeElementCode,
  signalementSousTypeElementLibelle: values.signalementSousTypeElementLibelle,
  signalementSousTypeElementActif: values.signalementSousTypeElementActif,
  signalementSousTypeElementTypeElement:
    values.signalementSousTypeElementTypeElement,
  signalementSousTypeElementTypeGeometrie:
    values.signalementSousTypeElementTypeGeometrie,
});

export const validationSchema = object({
  signalementSousTypeElementCode: requiredString,
  signalementSousTypeElementLibelle: requiredString,
  signalementSousTypeElementActif: requiredBoolean,
  // signalementSousTypeElementTypeElement: requiredString, // ISO V2
  signalementSousTypeElementTypeGeometrie: requiredString,
});
