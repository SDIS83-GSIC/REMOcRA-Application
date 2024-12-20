import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconImport } from "../../components/Icon/Icon.tsx";
import { URLS } from "../../routes.tsx";

export const getInitialValues = () => ({
  file: null,
});

export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("file", values.file);
  return formData;
};

export const validationSchema = object({});

const ImportCTP = () => {
  const navigate = useNavigate();
  return (
    <Container>
      <PageTitle
        icon={<IconImport />}
        title={"Importer un fichier de saisie CTP"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/importctp/verification`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={(result) =>
          navigate(URLS.RESULTAT_VERIF_IMPORT_CTP, {
            state: { result: result },
          })
        }
      >
        <FormImportCTP />
      </MyFormik>
    </Container>
  );
};

export default ImportCTP;

const FormImportCTP = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="file"
        accept=".xlsx"
        required={true}
        onChange={(e) => setFieldValue("file", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Vérifier les données
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};
