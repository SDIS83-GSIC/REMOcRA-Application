import { useFormikContext } from "formik";
import { object } from "yup";
import { Button, Col, Row } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { URLS } from "../../../routes.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";

export const getInitialValues = () => ({
  banniere: null,
  logo: null,
  symbologie: null,
  templateExportCtp: null,
});

export const validationSchema = object({});
export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("banniere", values.banniere);
  formData.append("logo", values.logo);
  formData.append("symbologie", values.symbologie);
  formData.append("templateExportCtp", values.templateExportCtp);
  return formData;
};

export const ImportRessources = () => {
  const { handleShowClose, activesKeys } = useAccordionState(
    Array(4).fill(false),
  );

  return (
    <>
      <h1>Import des ressources graphiques</h1>
      <p>
        Cet écran permet d&apos;importer différentes ressources utiles à
        l&apos;outil. Chaque bloc (<i>accordéon</i>) est indépendant, et possède
        son propre bouton d&apos;import.
      </p>

      <AccordionCustom
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
        list={[
          {
            header: "Importer la bannière",
            content: (
              <>
                <p>
                  Permet d&apos;importer le fichier image correspondant à la
                  bannière, au moins présent dans le bandeau (cf page
                  d&apos;accueil) et la page de connexion. <br />
                  Pour des raisons des performance, il conviendra d&apos;avoir
                  une taille minimale pour le visuel souhaité (max 200px de
                  haut).
                </p>
                <MyFormik
                  initialValues={getInitialValues()}
                  validationSchema={validationSchema}
                  isPost={false}
                  isMultipartFormData={true}
                  submitUrl={`/api/admin/import-banniere`}
                  prepareVariables={(values) => prepareVariables(values)}
                  redirectUrl={URLS.ADMIN_IMPORT_RESSOURCES}
                >
                  <FormImportBanniere />
                </MyFormik>
              </>
            ),
          },
          {
            header: "Importer le logo",
            content: (
              <>
                <p>
                  Permet d&apos;importer le fichier image correspondant au logo,
                  au moins présent dans le bandeau (cf page d&apos;accueil) et
                  la page de connexion. <br />
                  Pour des raisons des performance, il conviendra d&apos;avoir
                  une taille minimale pour le visuel souhaité (max 200px de
                  haut).
                </p>
                <MyFormik
                  initialValues={getInitialValues()}
                  validationSchema={validationSchema}
                  isPost={false}
                  isMultipartFormData={true}
                  submitUrl={`/api/admin/import-logo`}
                  prepareVariables={(values) => prepareVariables(values)}
                  redirectUrl={URLS.ADMIN_IMPORT_RESSOURCES}
                >
                  <FormImportLogo />
                </MyFormik>
              </>
            ),
          },
          {
            header: "Importer la symbologie",
            content: (
              <>
                <p>
                  Cette fonctionnalité permet d&apos;envoyer sur le serveur les
                  images correspondant à la symbologie des PEI ; <br />
                  l&apos;archive à envoyer contiendra à plat tous les fichiers
                  image correctement nommés afin que le mécanisme Geoserver
                  puisse correctement faire le mapping sur la carte.
                </p>
                <MyFormik
                  initialValues={getInitialValues()}
                  validationSchema={validationSchema}
                  isPost={false}
                  isMultipartFormData={true}
                  submitUrl={`/api/admin/import-symbologie`}
                  prepareVariables={(values) => prepareVariables(values)}
                  redirectUrl={URLS.ADMIN_IMPORT_RESSOURCES}
                >
                  <FormImportSymbologie />
                </MyFormik>
              </>
            ),
          },
          {
            header: "Importer le modèle d'Import-CTP",
            content: (
              <>
                <p>
                  Permet d&apos;importer le fichier .xlsx utilisé pour la
                  fonctionnalité d&apos;import CTP.
                  <br />
                  Ce fichier sera ensuite complété automatiquement par
                  l&apos;application à l&apos;aide des informations générales
                  des PEI lors d&apos;un export.
                  <br />
                  Attention, cette fonctionnalité s&apos;appuyant sur un fichier
                  XLSX avec des pseudo-contrats qui ne peuvent pas être
                  garantis, elle est à utiliser uniquement lorsque les méthodes
                  plus robustes ne sont pas applicables.
                </p>
                <MyFormik
                  initialValues={getInitialValues()}
                  validationSchema={validationSchema}
                  isPost={false}
                  isMultipartFormData={true}
                  submitUrl={`/api/admin/depot-template-ctp`}
                  prepareVariables={(values) => prepareVariables(values)}
                  redirectUrl={URLS.ADMIN_IMPORT_RESSOURCES}
                >
                  <FormImportTemplateExportCTP />
                </MyFormik>
              </>
            ),
          },
        ]}
      />
    </>
  );
};

const FormImportBanniere = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="banniere"
        required={true}
        onChange={(e) => setFieldValue("banniere", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Importer la bannière
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

const FormImportLogo = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="logo"
        required={true}
        onChange={(e) => setFieldValue("logo", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Importer le logo
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

const FormImportSymbologie = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="symbologie"
        required={true}
        accept=".zip"
        onChange={(e) => setFieldValue("symbologie", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Importer la symbologie
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

const FormImportTemplateExportCTP = () => {
  const { setFieldValue } = useFormikContext();

  return (
    <FormContainer>
      <FileInput
        name="templateExportCtp"
        accept=".xlsx"
        required={true}
        onChange={(e) => setFieldValue("templateExportCtp", e.target.files[0])}
      />
      <Row className="mt-3">
        <Col className="text-center">
          <Button type="submit" variant="primary">
            Importer le support
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};
