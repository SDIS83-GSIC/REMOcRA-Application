import { object } from "yup";
import {
  FormContainer,
  FieldSet,
  TextInput,
} from "../../../components/Form/Form.tsx";
import { requiredString } from "../../../module/validators.tsx";

export const ValidationSchema = object({
  docLibelle: requiredString,
});

export const getInitialValue = () => ({
  docLibelle: "",
});

export const prepareVariables = (
  document: any,
  values: docType,
  localisation: string,
) => {
  const formData = new FormData();
  formData.append("document", document);
  formData.append("criseDocName", values.docLibelle);
  formData.append("documentGeometry", JSON.stringify(localisation));

  return formData;
};

const AddTitleForm = () => {
  return (
    <FormContainer>
      <FieldSet title={"Ajouter un titre à la capture"}>
        <TextInput
          label="Nom de la capture"
          name="docLibelle"
          required={true}
        />
      </FieldSet>
    </FormContainer>
  );
};

type docType = {
  docLibelle: string;
};

export default AddTitleForm;
