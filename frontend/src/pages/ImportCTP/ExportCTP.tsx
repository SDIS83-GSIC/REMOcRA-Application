import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.js";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { IconExport } from "../../components/Icon/Icon.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";

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
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button
            onClick={() =>
              getFile(
                "/api/importctp/export",
                { communeId: values.communeId ?? null },
                `export-ctp${values.communeId ? `_${listeCommune.data.find((e) => e.id === values.communeId)?.code}` : ""}.xlsx`,
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

function getFile(
  urlApi: string,
  myObject: any,
  fileName: string,
  successToast: (e: string) => void,
  errorToast: (e) => void,
) {
  // On doit passer par un POST pour pouvoir envoyer la liste des paramètres
  fetch(
    url`${urlApi}`,
    getFetchOptions({
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ myObject }),
    }),
  )
    .then((response) => {
      if (!response.ok) {
        errorToast(response.text());
      }
      return response.blob();
    })
    .then((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = fileName; // Nom du fichier à télécharger

      a.click();
      window.URL.revokeObjectURL(url); // Libération de la mémoire
      successToast("Export terminé");
    });
}
