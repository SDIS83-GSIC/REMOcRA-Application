import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  IconExport,
  IconImport,
  IconInfo,
} from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { URLS } from "../../../routes.tsx";
import { downloadOutputFile } from "../../../utils/fonctionsUtils.tsx";
import { validationSchema } from "../accueil/AdminAccueil.tsx";

export const getInitialValues = () => ({
  document: null,
});

export const prepareVariables = (values: { document: string | Blob }) => {
  const formData = new FormData();
  formData.append("document", values.document);
  return formData;
};

const ImportUtilisateur = () => {
  const navigate = useNavigate();
  const location = useLocation();
  return (
    <Container>
      <PageTitle icon={<IconImport />} title={"Importer des utilisateurs"} />
      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/utilisateur/import/`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={(result) =>
          navigate(URLS.RESULTAT_VERIF_IMPORT_UTILISATEURS, {
            state: {
              ...location.state,
              result: result,
            },
          })
        }
      >
        <FormImportDocument />
      </MyFormik>
    </Container>
  );
};

const ColonnesAttendues = () => {
  return (
    <>
      Fichier csv :
      <TooltipCustom
        placement="right"
        tooltipText={
          <>
            <Row>
              <div>
                <span className="text-danger">*</span> : Champs obligatoires
              </div>
            </Row>
            <br />
            <Row>
              <div className="fw-bold">
                mail <span className="text-danger">*</span>
              </div>
              <div className="ms-2">L'adresse email de l'utilisateur</div>
            </Row>
            <Row>
              <div className="fw-bold">
                identifiant <span className="text-danger">*</span>
              </div>
              <div className="ms-2">
                L'identifiant (unique) de l'utilisateur
              </div>
            </Row>
            <Row>
              <div className="fw-bold">telephone</div>
              <div className="ms-2">
                Le numéro de téléphone de l'utilisateur
              </div>
            </Row>
            <Row>
              <div className="fw-bold">nom</div>
              <div className="ms-2">Le nom de l'utilisateur</div>
            </Row>

            <Row>
              <div className="fw-bold">prenom</div>
              <div className="ms-2">Le prénom de l'utilisateur</div>
            </Row>

            <Row>
              <div className="fw-bold">
                organisme <span className="text-danger">*</span>
              </div>
              <div className="ms-2">
                Le code de l'organisme associé à l'utilisateur
              </div>
            </Row>

            <Row>
              <div className="fw-bold">
                profil_utilisateur <span className="text-danger">*</span>
              </div>
              <div className="ms-2">
                Le code du profil utilisateur associé à l'utilisateur
              </div>
            </Row>

            <Row>
              <div className="fw-bold">actif</div>
              <div className="ms-2">
                Définit si un utilisateur doit être actif ou non dans
                l'application : "TRUE" / "FALSE". FALSE par défaut
              </div>
            </Row>

            <Row>
              <div className="fw-bold">notifie</div>
              <div className="ms-2">
                Définit si l'utilisateur doit être notifié : "TRUE" / "FALSE".
                FALSE par défaut
              </div>
            </Row>
          </>
        }
        tooltipHeader="Colonnes attendues"
        tooltipId={"importUtilisateur"}
      >
        <IconInfo />
      </TooltipCustom>
    </>
  );
};

const FormImportDocument = () => {
  const { setFieldValue } = useFormikContext();
  const { success: successToast, error: errorToast } = useToastContext();

  return (
    <>
      <Button
        className="mb-3"
        onClick={() =>
          downloadOutputFile(
            "/api/utilisateur/download-user-template",
            JSON.stringify({}),
            "import_utilisateur.csv",
            "Export terminé",
            successToast,
            errorToast,
          )
        }
      >
        Télécharger un modèle
        <IconExport />
      </Button>

      <FormContainer>
        <FileInput
          label={<ColonnesAttendues />}
          name="document"
          accept=".csv"
          required={true}
          onChange={(e) => setFieldValue("document", e.target.files[0])}
        />
        <Row className="mt-3">
          <Col className="text-center">
            <SubmitFormButtons submitTitle={"Vérifier"} />
          </Col>
        </Row>
      </FormContainer>
    </>
  );
};

export default ImportUtilisateur;
