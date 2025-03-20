import { Col, Row } from "react-bootstrap";
import { object } from "yup";
import {
  FormContainer,
  FieldSet,
  DateTimeInput,
  TextInput,
  TextAreaInput,
  RangeInput,
} from "../../../../components/Form/Form.tsx";
import {
  requiredDate,
  requiredString,
} from "../../../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../../../utils/formatDateUtils.tsx";

export const messageValidationSchema = object({
  messageObjet: requiredString,
  messageDescription: requiredString,
  messageDateConstat: requiredDate,
});

export const getInitialValue = (values: any, utilisateurId: any) => ({
  messageUtilisateurId: utilisateurId,
});

export const prepareMessageValues = (values: any) => ({
  messageObjet: values.messageObjet,
  messageDescription: values.messageDescription,
  messageDateConstat: new Date(values.messageDateConstat).toISOString(),
  messageImportance: values.messageImportance,
  messageOrigine: values.messageOrigine,
  messageTags: values.messageTags,
  messageUtilisateurId: values.messageUtilisateurId,
});

const MessageForm = () => {
  return (
    <FormContainer>
      <FieldSet title={"Nouveau message"}>
        <Row>
          <Col>
            <TextInput label="Objet" name={"messageObjet"} required={true} />
          </Col>
        </Row>

        <Row>
          <Col>
            <TextAreaInput
              label="Description"
              name={"messageDescription"}
              required={true}
            />
          </Col>
        </Row>

        <Row>
          <Col>
            <DateTimeInput
              label="Date de constat"
              name={"messageDateConstat"}
              required={true}
              value={formatDateTimeForDateTimeInput(new Date())}
            />
          </Col>

          <Col>
            <RangeInput
              value={0}
              step={1}
              min={0}
              name={"messageImportance"}
              label="Importance"
              max={5}
            />
          </Col>
        </Row>

        <Row>
          <Col>
            <TextInput
              label="Origine"
              name={"messageOrigine"}
              required={false}
            />
          </Col>
          <Col>
            <TextInput required={false} label="Tags" name={"messageTags"} />
          </Col>
        </Row>
      </FieldSet>
    </FormContainer>
  );
};

export default MessageForm;
