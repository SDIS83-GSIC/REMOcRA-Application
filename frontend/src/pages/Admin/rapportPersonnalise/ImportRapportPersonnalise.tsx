import { useFormikContext } from "formik";
import { Container } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconImport } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { requiredFile } from "../../../module/validators.tsx";
import { URLS } from "../../../routes.tsx";

const getInitialValues = () => ({
  zipFile: undefined,
});

export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("zipFile", values.zipFile);
  return formData;
};

const validationSchema = object({
  zipFile: requiredFile,
});

const ImportRapportPersonnalise = () => {
  return (
    <>
      <Container>
        <PageTitle
          title={"Importer un rapport personnalisé"}
          icon={<IconImport />}
        />
        <MyFormik
          initialValues={getInitialValues()}
          validationSchema={validationSchema}
          prepareVariables={prepareVariables}
          submitUrl={url`/api/rapport-personnalise/import`}
          isMultipartFormData={true}
          redirectUrl={URLS.LIST_RAPPORT_PERSONNALISE}
        >
          <ImportRapportPersonnaliseForm />
        </MyFormik>
      </Container>
    </>
  );
};

const ImportRapportPersonnaliseForm = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name={"zipFile"}
        label={"Fichier ZIP"}
        accept={".zip"}
        onChange={(e) => {
          setFieldValue("zipFile", e.target.files[0]);
        }}
      />
      <SubmitFormButtons returnLink={URLS.LIST_RAPPORT_PERSONNALISE} />
    </FormContainer>
  );
};

export default ImportRapportPersonnalise;
