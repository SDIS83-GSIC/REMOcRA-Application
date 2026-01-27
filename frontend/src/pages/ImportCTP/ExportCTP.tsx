import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { IconExport } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { downloadOutputFile } from "../../utils/fonctionsUtils.tsx";

export const getInitialValues = () => ({
  communeId: null,
});

export const prepareVariables = (values) => ({
  communeId: values.communeId ?? null,
});

export const validationSchema = object({});

const ExportCTP = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconExport />}
        title={"Exporter des données pour la saisie CTP"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={false}
        submitUrl={`/api/importctp/export`}
        prepareVariables={(values) => prepareVariables(values)}
      >
        <FormExportCTP />
      </MyFormik>
    </Container>
  );
};

export default ExportCTP;

const FormExportCTP = () => {
  const { values, setValues } = useFormikContext();
  const { success: successToast, error: errorToast } = useToastContext();
  const listeCommune = useGet(url`/api/commune/get-libelle-commune`);

  if (!listeCommune.isResolved) {
    return;
  }

  return (
    <FormContainer>
      <SelectForm
        name="communeId"
        label="Commune"
        listIdCodeLibelle={listeCommune.data}
        required={false}
        setValues={setValues}
        defaultValue={listeCommune.data.find((e) => e.id === values.communeId)}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button
            onClick={() =>
              downloadOutputFile(
                "/api/importctp/export",
                JSON.stringify({ communeId: values.communeId ?? null }),
                `export-ctp${values.communeId ? `_${listeCommune.data.find((e) => e.id === values.communeId)?.libelle}` : ""}.xlsx`,
                "Export terminé",
                successToast,
                errorToast,
              )
            }
            disabled={false}
          >
            Exporter et télécharger
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};
