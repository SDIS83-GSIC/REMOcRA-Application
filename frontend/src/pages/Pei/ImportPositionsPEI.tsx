import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import {
  IconExport,
  IconImport,
  IconInfo,
} from "../../components/Icon/Icon.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { URLS } from "../../routes.tsx";
import { downloadOutputFile } from "../../utils/fonctionsUtils.tsx";

export const getInitialValues = () => ({
  file: null,
});

export const prepareVariables = (values: { file: File | Blob }) => {
  const formData = new FormData();
  formData.append("file", values.file);
  return formData;
};

export const validationSchema = object({});

const ImportPositionsPEI = () => {
  const navigate = useNavigate();
  const location = useLocation();
  return (
    <Container>
      <PageTitle
        icon={<IconImport />}
        title={"Importer un fichier de mise à jour des positions des PEI"}
      />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/maj-positions-pei/verification`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={(result) =>
          navigate(URLS.RESULTAT_VERIF_MAJ_POSITIONS_PEI, {
            state: {
              ...location.state,
              result: result,
            },
          })
        }
      >
        <FormImportPositionsPEI />
      </MyFormik>
    </Container>
  );
};

export default ImportPositionsPEI;

const FormImportPositionsPEI = () => {
  const { setFieldValue } = useFormikContext();
  const { success: successToast, error: errorToast } = useToastContext();

  return (
    <>
      <Button
        className="mb-3"
        onClick={() =>
          downloadOutputFile(
            "/api/maj-positions-pei/download-template",
            JSON.stringify({}),
            "deplacement-pei.csv",
            "Export terminé",
            successToast,
            errorToast,
          )
        }
      >
        Télécharger un modèle de fichier CSV &nbsp;
        <IconExport />
      </Button>

      <FormContainer>
        <FileInput
          label={
            <>
              Fichier CSV
              <TooltipCustom
                placement="right"
                tooltipText={
                  <>
                    <Row>
                      <div className="fw-bold">
                        EPSG <span className="text-danger">*</span>
                      </div>
                      <div className="ms-2">
                        Le code EPSG du système de coordonnées.
                      </div>
                    </Row>
                    <Row>
                      <div className="fw-bold">
                        X <span className="text-danger">*</span>
                      </div>
                      <div className="ms-2">
                        La coordonnée X de la position du PEI.
                      </div>
                    </Row>
                    <Row>
                      <div className="fw-bold">
                        Y <span className="text-danger">*</span>
                      </div>
                      <div className="ms-2">
                        La coordonnée Y de la position du PEI.
                      </div>
                    </Row>
                    <Row>
                      <div className="fw-bold">DATE_GPS</div>
                      <div className="ms-2">
                        La date et l'heure de la mesure GPS, format "dd/MM/yy
                        HH:mm" (exemple : 31/12/23 14:30).
                      </div>
                    </Row>
                    <Row>
                      <div className="fw-bold">OBSERVATION</div>
                      <div className="ms-2">
                        La description de l'observation du PEI.
                      </div>
                    </Row>
                    <Row>
                      <div className="fw-bold">
                        NUMERO <span className="text-danger">*</span>
                      </div>
                      <div className="ms-2">Le numéro du PEI.</div>
                    </Row>
                    <br />
                    <Row>
                      <div>
                        <span className="text-danger">*</span> : Champs
                        obligatoires
                      </div>
                    </Row>
                  </>
                }
                tooltipHeader="Colonnes attendues"
                tooltipId={"importCSV"}
              >
                <IconInfo />
              </TooltipCustom>
            </>
          }
          name="file"
          accept=".csv"
          required={true}
          onChange={(e) => setFieldValue("file", e.target.files[0])}
        />
        <Row className="mt-3">
          <Col className="text-center">
            <SubmitFormButtons submitTitle="Vérifier les données" />
          </Col>
        </Row>
      </FormContainer>
    </>
  );
};
