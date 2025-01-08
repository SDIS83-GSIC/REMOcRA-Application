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
});

export const validationSchema = object({});
export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append("banniere", values.banniere);
  formData.append("logo", values.logo);
  return formData;
};

export const ImportRessources = () => {
  const { handleShowClose, activesKeys } = useAccordionState(
    Array(3).fill(false),
  );

  return (
    <>
      <h1>Import des ressources graphiques</h1>
      <p>
        Cet écran permet d&apos;importer différentes ressources graphiques
        utiles à l&apos;outil. Chaque bloc (<i>accordéon</i>) est indépendant,
        et possède son propre bouton d&apos;import.
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
