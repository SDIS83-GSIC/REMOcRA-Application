import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer, TextInput } from "../../Form/Form.tsx";
import MyFormik from "../../Form/MyFormik.tsx";
import { IconImport } from "../../Icon/Icon.tsx";

export const getInitialValues = () => ({
  fileKml: null,
  risqueLibelle: "",
});

export const validationSchema = object({});

export const prepareVariables = (values: {
  fileKml: Blob | null;
  risqueLibelle: string;
}) => {
  const formData = new FormData();
  if (values.fileKml) {
    formData.append("fileKml", values.fileKml);
  }
  if (values.risqueLibelle) {
    formData.append("risqueLibelle", values.risqueLibelle);
  }
  return formData;
};

const ImportKml = () => {
  return (
    <Container>
      <PageTitle icon={<IconImport />} title={"Importer un fichier KML"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={true}
        submitUrl={`/api/risque/import/`}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.RISQUE}
        onSubmit={() => {}}
      >
        <FormImportKml />
      </MyFormik>
    </Container>
  );
};

const FormImportKml = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <TextInput
        name="risqueLibelle"
        label="Libellé du risque"
        required={false}
      />
      <FileInput
        name="fileKml"
        accept=".kml"
        label={<>Fichier KML contenant les géométries à intégrer</>}
        required={false}
        onChange={(e) => setFieldValue("fileKml", e.target.files[0])}
      />
      <br />

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

export default ImportKml;
