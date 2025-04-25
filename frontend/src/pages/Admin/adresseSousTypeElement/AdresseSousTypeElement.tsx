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
import { referenceTypeGeometrie } from "../../../enums/Adresse/SousTypeTypeGeometrie.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

const AdresseSousTypeElement = () => {
  const { values, setValues } = useFormikContext<AdresseSousTypeElementType>();
  const listeTypeElement: IdCodeLibelleType[] = useGet(
    url`/api/adresse-sous-type-element/ref`,
  ).data;

  return (
    <FormContainer>
      <TextInput
        label="Code"
        name="adresseSousTypeElementCode"
        required={true}
      />
      <TextInput
        label="Libellé"
        name="adresseSousTypeElementLibelle"
        required={true}
      />
      <CheckBoxInput name="adresseSousTypeElementActif" label="Actif" />
      <SelectForm
        name="adresseSousTypeElementTypeElement"
        label="Type élément parent"
        listIdCodeLibelle={listeTypeElement}
        setValues={setValues}
        defaultValue={listeTypeElement?.find(
          (e) => e.id === values.adresseSousTypeElementTypeElement,
        )}
        required={false} // ISO V2
      />
      <SelectForm
        name="adresseSousTypeElementTypeGeometrie"
        label="Type de géométrie"
        listIdCodeLibelle={referenceTypeGeometrie}
        setValues={setValues}
        defaultValue={referenceTypeGeometrie?.find(
          (e) => e.id === values.adresseSousTypeElementTypeGeometrie,
        )}
        required={true} // ISO V2
      />
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default AdresseSousTypeElement;

type AdresseSousTypeElementType = {
  adresseSousTypeElementCode: string;
  adresseSousTypeElementLibelle: string;
  adresseSousTypeElementActif: boolean;
  adresseSousTypeElementTypeElement?: string;
  adresseSousTypeElementTypeGeometrie: string;
};

export const getInitialValue = () => ({
  adresseSousTypeElementCode: null,
  adresseSousTypeElementLibelle: null,
  adresseSousTypeElementActif: false,
  adresseSousTypeElementTypeElement: null,
  adresseSousTypeElementTypeGeometrie: null,
});

export const prepareValues = (values: AdresseSousTypeElementType) => ({
  adresseSousTypeElementCode: values.adresseSousTypeElementCode,
  adresseSousTypeElementLibelle: values.adresseSousTypeElementLibelle,
  adresseSousTypeElementActif: values.adresseSousTypeElementActif,
  adresseSousTypeElementTypeElement: values.adresseSousTypeElementTypeElement,
  adresseSousTypeElementTypeGeometrie:
    values.adresseSousTypeElementTypeGeometrie,
});

export const validationSchema = object({
  adresseSousTypeElementCode: requiredString,
  adresseSousTypeElementLibelle: requiredString,
  adresseSousTypeElementActif: requiredBoolean,
  // adresseSousTypeElementTypeElement: requiredString, // ISO V2
  adresseSousTypeElementTypeGeometrie: requiredString,
});
