import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import { useFormikContext } from "formik";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconImport } from "../../components/Icon/Icon.tsx";
import { URLS } from "../../routes.tsx";
import { FileInput, FormContainer } from "../../components/Form/Form.tsx";

export const getInitialValues = () => ({
  document: null,
});

export const validationSchema = object({});

export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("document", values.document);
  return formData;
};

const DepotDeliberation = () => {
  return (
    <Container>
      <PageTitle icon={<IconImport />} title={"Déposer une délibération"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/adresses/deliberation/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.ACCUEIL}
      >
        <FormImportDocument />
      </MyFormik>
    </Container>
  );
};

const FormImportDocument = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="document"
        accept=".*"
        required={true}
        onChange={(e) => setFieldValue("document", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Valider
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

export default DepotDeliberation;
