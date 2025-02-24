import { useFormikContext } from "formik";
import { object } from "yup";
import {
  FormContainer,
  FieldSet,
  TextInput,
} from "../../../components/Form/Form.tsx";
import FormDocuments, {
  setDocumentInFormData,
} from "../../../components/Form/FormDocuments.tsx";
import EvenementType from "../../../Entities/EvenementEntity.tsx";

export const ValidationSchema = object({});

export const getInitialValue = () => ({
  documents: [],
});

export const prepareVariables = (values: EvenementType) => {
  const formData = new FormData();
  setDocumentInFormData(values?.documents, null, formData);
  return formData;
};

const MessageForm = () => {
  const { setFieldValue, values }: { values: EvenementType } =
    useFormikContext();
  return (
    <FormContainer>
      <FieldSet title={"Ajouter un document"}>
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
      </FieldSet>
    </FormContainer>
  );
};

export default MessageForm;
